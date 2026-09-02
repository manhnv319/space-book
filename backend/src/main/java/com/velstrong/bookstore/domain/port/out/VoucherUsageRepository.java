package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.VoucherUsage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherUsageRepository {
    VoucherUsage save(VoucherUsage voucherUsage);
    Optional<VoucherUsage> findById(Long id);
    Optional<VoucherUsage> findReservedByOrderId(Long orderId);
    int countCommittedByVoucherId(Long voucherId);
    int countCommittedByVoucherIdAndUserId(Long voucherId, Long userId);
    List<VoucherUsage> findExpiredReservations(LocalDateTime before);
}
