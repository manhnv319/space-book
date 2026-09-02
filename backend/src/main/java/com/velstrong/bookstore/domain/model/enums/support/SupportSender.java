package com.velstrong.bookstore.domain.model.enums.support;

public enum SupportSender {
    CUSTOMER,
    STAFF;

    public boolean isCustomer() { return this == CUSTOMER; }
}
