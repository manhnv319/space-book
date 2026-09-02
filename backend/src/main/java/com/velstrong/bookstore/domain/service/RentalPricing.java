package com.velstrong.bookstore.domain.service;

import com.velstrong.bookstore.domain.model.Book;

/**
 * Authoritative rental pricing derived from the Book (server-side; client-sent prices are ignored).
 * Fee = per-unit rental price (by term unit) × number of terms. Deposit comes straight from the Book.
 */
public final class RentalPricing {

    private RentalPricing() {}

    public static long rentalFee(Book book, Integer termValue, String termUnit) {
        int terms = termValue != null && termValue > 0 ? termValue : 1;
        String unit = termUnit != null ? termUnit.trim().toUpperCase() : "WEEK";

        Long unitPrice = unitPriceFor(book, unit);
        if (unitPrice != null && unitPrice > 0) {
            return unitPrice * terms;
        }
        // Fallback when the chosen unit has no price: derive from the daily rate scaled to the
        // unit's day-count (WEEK≈7, MONTH≈30) so the estimate stays in the right ballpark.
        Long dayPrice = book.getRentalPriceDay();
        if (dayPrice == null || dayPrice <= 0) {
            return 0L;
        }
        return dayPrice * daysPerUnit(unit) * terms;
    }

    public static long deposit(Book book) {
        return book.getDepositAmount() != null ? book.getDepositAmount() : 0L;
    }

    private static Long unitPriceFor(Book book, String unit) {
        return switch (unit) {
            case "DAY" -> book.getRentalPriceDay();
            case "MONTH" -> book.getRentalPriceMonth();
            default -> book.getRentalPriceWeek();
        };
    }

    private static long daysPerUnit(String unit) {
        return switch (unit) {
            case "DAY" -> 1L;
            case "MONTH" -> 30L;
            default -> 7L;
        };
    }
}
