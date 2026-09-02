package com.velstrong.bookstore.domain.model.enums.order;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPING,
    COMPLETED,
    CANCELLED,
    REFUNDED;

    public boolean isPending() { return this == PENDING; }
    public boolean isConfirmed() { return this == CONFIRMED; }
    public boolean isCompleted() { return this == COMPLETED; }
    public boolean isCancelled() { return this == CANCELLED; }
    public boolean canBeCancelled() { return this == PENDING || this == CONFIRMED; }
}
