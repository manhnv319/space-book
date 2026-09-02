package com.velstrong.bookstore.domain.model.enums.order;

public enum OrderType {
    PURCHASE,
    RENTAL,
    MIXED;

    public boolean isPurchase() { return this == PURCHASE; }
    public boolean isRental() { return this == RENTAL; }
    public boolean isMixed() { return this == MIXED; }
}
