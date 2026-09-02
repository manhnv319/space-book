package com.velstrong.bookstore.domain.model.enums.order;

public enum PaymentStatus {
    UNPAID,
    PARTIALLY_PAID,
    PAID,
    REFUNDING,
    REFUNDED;

    public boolean isUnpaid() { return this == UNPAID; }
    public boolean isPaid() { return this == PAID; }
    public boolean isRefunded() { return this == REFUNDED; }
}
