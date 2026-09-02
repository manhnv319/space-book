package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.command.voucher.ReserveVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReserveVoucherServiceTest {

    private final VoucherRepository voucherRepository = mock(VoucherRepository.class);
    private final VoucherUsageRepository voucherUsageRepository = mock(VoucherUsageRepository.class);
    private final QuoteVoucherUseCase quoteVoucherUseCase = mock(QuoteVoucherUseCase.class);
    private final ReserveVoucherService service = new ReserveVoucherService(
            voucherRepository, voucherUsageRepository, quoteVoucherUseCase);

    @Test
    void reservesVoucherWhenAtomicClaimSucceeds() {
        Voucher voucher = Voucher.reconstitute(1L, "SALE10", "Sale 10%", null,
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 100, 1, 0, (byte) 1);
        ReserveVoucherCommand command = new ReserveVoucherCommand(7L, "SALE10", 42L, 100_000L);
        when(quoteVoucherUseCase.quote(any())).thenReturn(VoucherQuoteResponse.valid(10_000L, 90_000L));
        when(voucherRepository.findByCode("SALE10")).thenReturn(Optional.of(voucher));
        when(voucherRepository.tryIncrementUsage(1L)).thenReturn(true);

        var response = service.reserve(command);

        assertEquals(10_000L, response.discountAmount());
        ArgumentCaptor<VoucherUsage> captor = ArgumentCaptor.forClass(VoucherUsage.class);
        verify(voucherUsageRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getVoucherId());
        assertEquals(7L, captor.getValue().getUserId());
        assertEquals(42L, captor.getValue().getOrderId());
    }

    @Test
    void rejectsReservationWhenUsageLimitAlreadyReached() {
        Voucher voucher = Voucher.reconstitute(1L, "SALE10", "Sale 10%", null,
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 100, 1, 100, (byte) 1);
        ReserveVoucherCommand command = new ReserveVoucherCommand(7L, "SALE10", 42L, 100_000L);
        when(quoteVoucherUseCase.quote(any())).thenReturn(VoucherQuoteResponse.valid(10_000L, 90_000L));
        when(voucherRepository.findByCode("SALE10")).thenReturn(Optional.of(voucher));
        when(voucherRepository.tryIncrementUsage(1L)).thenReturn(false);

        assertThrows(InvalidOperationException.class, () -> service.reserve(command));
        verify(voucherUsageRepository, never()).save(any());
    }

    @Test
    void rejectsReservationWhenQuoteIsInvalid() {
        ReserveVoucherCommand command = new ReserveVoucherCommand(7L, "SALE10", 42L, 100_000L);
        when(quoteVoucherUseCase.quote(any(QuoteVoucherCommand.class)))
                .thenReturn(VoucherQuoteResponse.invalid(VoucherValidationReason.EXPIRED));

        assertThrows(InvalidOperationException.class, () -> service.reserve(command));
        verify(voucherRepository, never()).tryIncrementUsage(any());
        verify(voucherUsageRepository, never()).save(any());
    }
}
