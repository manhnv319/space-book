package com.velstrong.bookstore.domain.model.enums.order;

public enum ItemType {
    PURCHASE,
    RENTAL;

    public boolean isPurchase() { return this == PURCHASE; }
    public boolean isRental() { return this == RENTAL; }
}
