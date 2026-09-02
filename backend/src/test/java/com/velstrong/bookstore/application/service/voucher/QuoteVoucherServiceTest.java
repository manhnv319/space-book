package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuoteVoucherServiceTest {

    private VoucherRepository voucherRepository;
    private VoucherUsageRepository voucherUsageRepository;
    private QuoteVoucherService service;

    @BeforeEach
    void setUp() {
        voucherRepository = mock(VoucherRepository.class);
        voucherUsageRepository = mock(VoucherUsageRepository.class);
        service = new QuoteVoucherService(voucherRepository, voucherUsageRepository);
    }

    @Test
    @DisplayName("returns NOT_FOUND when voucher does not exist")
    void notFound() {
        when(voucherRepository.findByCode("NOPE")).thenReturn(Optional.empty());
        var response = service.quote(new QuoteVoucherCommand(7L, "NOPE", 100_000L));
        assertThat(response.valid()).isFalse();
        assertThat(response.reason()).isEqualTo(VoucherValidationReason.NOT_FOUND);
    }

    @Test
    @DisplayName("returns MIN_ORDER_NOT_MET when base below min order amount")
    void minOrderNotMet() {
        Voucher v = Voucher.create("MIN", "min", VoucherDiscountType.PERCENTAGE,
                10L, null, 200_000L, null, null, 100, 1);
        when(voucherRepository.findByCode("MIN")).thenReturn(Optional.of(v));
        var response = service.quote(new QuoteVoucherCommand(7L, "MIN", 50_000L));
        assertThat(response.reason()).isEqualTo(VoucherValidationReason.MIN_ORDER_NOT_MET);
    }

    @Test
    @DisplayName("returns EXPIRED when now is after endAt")
    void expired() {
        Voucher v = Voucher.create("EXP", "exp", VoucherDiscountType.PERCENTAGE,
                10L, null, 0L, null, LocalDateTime.now().minusDays(1), 100, 1);
        when(voucherRepository.findByCode("EXP")).thenReturn(Optional.of(v));
        var response = service.quote(new QuoteVoucherCommand(7L, "EXP", 100_000L));
        assertThat(response.reason()).isEqualTo(VoucherValidationReason.EXPIRED);
    }

    @Test
    @DisplayName("returns USAGE_LIMIT_REACHED when committed count hits limit")
    void usageLimitReached() {
        Voucher v = Voucher.reconstitute(1L, "LIM", "lim", null,
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 1, 1, 0, (byte) 1);
        when(voucherRepository.findByCode("LIM")).thenReturn(Optional.of(v));
        when(voucherUsageRepository.countCommittedByVoucherId(1L)).thenReturn(1);
        var response = service.quote(new QuoteVoucherCommand(7L, "LIM", 100_000L));
        assertThat(response.reason()).isEqualTo(VoucherValidationReason.USAGE_LIMIT_REACHED);
    }

    @Test
    @DisplayName("returns USER_LIMIT_REACHED when user has used too many times")
    void userLimitReached() {
        Voucher v = Voucher.reconstitute(1L, "ULIM", "ulim", null,
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 100, 1, 0, (byte) 1);
        when(voucherRepository.findByCode("ULIM")).thenReturn(Optional.of(v));
        when(voucherUsageRepository.countCommittedByVoucherId(1L)).thenReturn(0);
        when(voucherUsageRepository.countCommittedByVoucherIdAndUserId(1L, 7L)).thenReturn(1);
        var response = service.quote(new QuoteVoucherCommand(7L, "ULIM", 100_000L));
        assertThat(response.reason()).isEqualTo(VoucherValidationReason.USER_LIMIT_REACHED);
    }

    @Test
    @DisplayName("returns valid quote with discount when voucher is usable")
    void valid() {
        Voucher v = Voucher.reconstitute(1L, "SALE10", "ok", null,
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 100, 1, 0, (byte) 1);
        when(voucherRepository.findByCode("SALE10")).thenReturn(Optional.of(v));
        when(voucherUsageRepository.countCommittedByVoucherId(1L)).thenReturn(0);
        var response = service.quote(new QuoteVoucherCommand(7L, "SALE10", 200_000L));
        assertThat(response.valid()).isTrue();
        assertThat(response.discountAmount()).isEqualTo(20_000L);
        assertThat(response.finalAmount()).isEqualTo(180_000L);
    }
}
