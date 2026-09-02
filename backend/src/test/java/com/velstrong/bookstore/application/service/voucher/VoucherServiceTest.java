package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.domain.exception.DuplicateEntityException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherUsageStatus;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VoucherServiceTest {

    private VoucherRepository voucherRepository;
    private VoucherUsageRepository voucherUsageRepository;
    private CreateVoucherService createService;
    private UpdateVoucherService updateService;
    private CommitVoucherService commitService;
    private CancelVoucherService cancelService;

    @BeforeEach
    void setUp() {
        voucherRepository = mock(VoucherRepository.class);
        voucherUsageRepository = mock(VoucherUsageRepository.class);
        createService = new CreateVoucherService(voucherRepository);
        updateService = new UpdateVoucherService(voucherRepository);
        commitService = new CommitVoucherService(voucherUsageRepository);
        cancelService = new CancelVoucherService(voucherUsageRepository, voucherRepository);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(inv -> {
            Voucher v = inv.getArgument(0);
            return Voucher.reconstitute(1L, v.getCode(), v.getName(), v.getDescription(),
                    v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount(),
                    v.getMinOrderAmount(), v.getStartAt(), v.getEndAt(),
                    v.getUsageLimitTotal(), v.getUsageLimitPerUser(), v.getUsedCount(), v.getStatus());
        });
    }

    @Test
    @DisplayName("createVoucher rejects duplicate code")
    void createVoucherRejectsDuplicate() {
        when(voucherRepository.findByCode("DUP")).thenReturn(Optional.of(
                Voucher.create("DUP", "x", VoucherDiscountType.PERCENTAGE, 10L, null, 0L, null, null, 1, 1)));
        var command = baseCommand("DUP");

        assertThatThrownBy(() -> createService.create(command))
                .isInstanceOf(DuplicateEntityException.class);
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("createVoucher persists when code is unique")
    void createVoucherSucceeds() {
        when(voucherRepository.findByCode("NEW")).thenReturn(Optional.empty());
        var response = createService.create(baseCommand("NEW"));
        assertThat(response.code()).isEqualTo("NEW");
        assertThat(response.usageLimitTotal()).isEqualTo(100);
    }

    @Test
    @DisplayName("updateVoucher throws when not found")
    void updateVoucherMissing() {
        when(voucherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateService.update(99L, baseCommand("X")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("updateVoucher mutates the loaded voucher before save")
    void updateVoucherMutates() {
        Voucher existing = Voucher.create("SALE10", "old", VoucherDiscountType.PERCENTAGE,
                10L, null, 0L, null, null, 100, 1);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(existing));

        var response = updateService.update(1L, new CreateVoucherCommand(
                "SALE10", "new name", "new desc",
                VoucherDiscountType.PERCENTAGE, 20L, 100_000L, 0L,
                null, null, 200, 2));

        assertThat(existing.getName()).isEqualTo("new name");
        assertThat(existing.getDiscountValue()).isEqualTo(20L);
        assertThat(existing.getUsageLimitTotal()).isEqualTo(200);
        assertThat(response.name()).isEqualTo("new name");
    }

    @Test
    @DisplayName("commitVoucher marks the reserved usage as COMMITTED")
    void commitVoucher() {
        VoucherUsage usage = VoucherUsage.reserve(1L, 7L, 99L, 10_000L);
        when(voucherUsageRepository.findReservedByOrderId(99L)).thenReturn(Optional.of(usage));
        when(voucherUsageRepository.save(any(VoucherUsage.class))).thenAnswer(inv -> inv.getArgument(0));

        commitService.commit(new com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand(99L));

        ArgumentCaptor<VoucherUsage> captor = ArgumentCaptor.forClass(VoucherUsage.class);
        verify(voucherUsageRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(VoucherUsageStatus.COMMITTED);
    }

    @Test
    @DisplayName("commitVoucher is a no-op when no reserved usage exists")
    void commitVoucherNoOp() {
        when(voucherUsageRepository.findReservedByOrderId(99L)).thenReturn(Optional.empty());

        commitService.commit(new com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand(99L));

        verify(voucherUsageRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelVoucher cancels and decrements voucher usedCount")
    void cancelVoucher() {
        VoucherUsage usage = VoucherUsage.reserve(1L, 7L, 99L, 10_000L);
        when(voucherUsageRepository.findReservedByOrderId(99L)).thenReturn(Optional.of(usage));
        when(voucherUsageRepository.save(any(VoucherUsage.class))).thenAnswer(inv -> inv.getArgument(0));
        when(voucherRepository.decrementUsage(1L)).thenReturn(true);

        cancelService.cancelReservation(99L);

        assertThat(usage.getStatus()).isEqualTo(VoucherUsageStatus.CANCELLED);
        verify(voucherRepository).decrementUsage(1L);
    }

    @Test
    @DisplayName("cancelVoucher is a no-op when no reserved usage exists")
    void cancelVoucherNoOp() {
        when(voucherUsageRepository.findReservedByOrderId(99L)).thenReturn(Optional.empty());

        cancelService.cancelReservation(99L);

        verify(voucherRepository, never()).decrementUsage(any());
    }

    private static CreateVoucherCommand baseCommand(String code) {
        return new CreateVoucherCommand(code, "name", "desc",
                VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                null, null, 100, 1);
    }
}
