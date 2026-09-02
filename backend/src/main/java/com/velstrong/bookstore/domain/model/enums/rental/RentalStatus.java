package com.velstrong.bookstore.domain.model.enums.rental;

public enum RentalStatus {
    PENDING,
    RENTED,
    RETURNED,
    LATE,
    LOST,
    CANCELLED;

    public boolean isActive() { return this == RENTED || this == LATE; }
    public boolean isRented() { return this == RENTED; }
    public boolean isLate() { return this == LATE; }
    public boolean isReturned() { return this == RETURNED; }
}
