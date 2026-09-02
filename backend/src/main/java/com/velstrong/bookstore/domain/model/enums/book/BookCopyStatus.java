package com.velstrong.bookstore.domain.model.enums.book;

public enum BookCopyStatus {
    AVAILABLE,
    RENTED,
    SOLD,
    DAMAGED,
    LOST,
    MAINTENANCE;

    public boolean isAvailable() { return this == AVAILABLE; }
    public boolean isRented() { return this == RENTED; }
}
