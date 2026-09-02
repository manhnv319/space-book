package com.velstrong.bookstore.domain.model.enums.subscription;

public enum SubscriptionStatus {
    ACTIVE,
    INACTIVE,
    DISCONTINUED;

    public boolean isActive() { return this == ACTIVE; }
}
