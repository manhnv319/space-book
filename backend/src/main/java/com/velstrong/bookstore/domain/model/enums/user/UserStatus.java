package com.velstrong.bookstore.domain.model.enums.user;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BANNED,
    PENDING_VERIFICATION;

    public boolean isActive() { return this == ACTIVE; }
    public boolean isBanned() { return this == BANNED; }
}
