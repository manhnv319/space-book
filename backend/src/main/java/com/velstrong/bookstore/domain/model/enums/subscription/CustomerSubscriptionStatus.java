package com.velstrong.bookstore.domain.model.enums.subscription;

public enum CustomerSubscriptionStatus {
    /** Đã đặt mua nhưng tiền chưa về — chưa được dùng để thuê sách. */
    PENDING_PAYMENT,
    ACTIVE,
    EXPIRED,
    CANCELLED;

    public boolean isActive() { return this == ACTIVE; }
    public boolean isAwaitingPayment() { return this == PENDING_PAYMENT; }
    public boolean isExpired() { return this == EXPIRED; }
    public boolean isCancelled() { return this == CANCELLED; }
}
