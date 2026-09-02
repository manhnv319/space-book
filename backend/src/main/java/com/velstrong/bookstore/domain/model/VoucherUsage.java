package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.voucher.VoucherUsageStatus;

import java.time.LocalDateTime;

public class VoucherUsage {

    private final Long id;
    private final Long voucherId;
    private final Long userId;
    private final Long orderId;
    private final Long discountAmount;
    private VoucherUsageStatus status;
    private final LocalDateTime reservedAt;
    private LocalDateTime committedAt;
    private LocalDateTime expiredAt;

    private VoucherUsage(Long id, Long voucherId, Long userId, Long orderId, Long discountAmount,
                         VoucherUsageStatus status, LocalDateTime reservedAt,
                         LocalDateTime committedAt, LocalDateTime expiredAt) {
        this.id = id;
        this.voucherId = voucherId;
        this.userId = userId;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
        this.status = status;
        this.reservedAt = reservedAt;
        this.committedAt = committedAt;
        this.expiredAt = expiredAt;
    }

    public static VoucherUsage reserve(Long voucherId, Long userId, Long orderId, Long discountAmount) {
        return new VoucherUsage(null, voucherId, userId, orderId, discountAmount,
                VoucherUsageStatus.RESERVED, LocalDateTime.now(), null, null);
    }

    public static VoucherUsage reconstitute(Long id, Long voucherId, Long userId, Long orderId,
                                            Long discountAmount, VoucherUsageStatus status,
                                            LocalDateTime reservedAt, LocalDateTime committedAt,
                                            LocalDateTime expiredAt) {
        return new VoucherUsage(id, voucherId, userId, orderId, discountAmount,
                status, reservedAt, committedAt, expiredAt);
    }

    public void commit() {
        this.status = VoucherUsageStatus.COMMITTED;
        this.committedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = VoucherUsageStatus.CANCELLED;
    }

    public void expire() {
        this.status = VoucherUsageStatus.EXPIRED;
        this.expiredAt = LocalDateTime.now();
    }

    public boolean isReserved() { return status != null && status.isReserved(); }

    public Long getId() { return id; }
    public Long getVoucherId() { return voucherId; }
    public Long getUserId() { return userId; }
    public Long getOrderId() { return orderId; }
    public Long getDiscountAmount() { return discountAmount; }
    public VoucherUsageStatus getStatus() { return status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public LocalDateTime getCommittedAt() { return committedAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
}
