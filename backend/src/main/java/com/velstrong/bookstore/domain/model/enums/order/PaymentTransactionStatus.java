package com.velstrong.bookstore.domain.model.enums.order;

public enum PaymentTransactionStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    CANCELLED;

    public boolean isPending() { return this == PENDING; }
    public boolean isSuccess() { return this == SUCCESS; }
    public boolean isFailed() { return this == FAILED; }
}
