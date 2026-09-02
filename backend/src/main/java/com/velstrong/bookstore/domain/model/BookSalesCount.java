package com.velstrong.bookstore.domain.model;

/**
 * Aggregate result of {@code SUM(order_items.quantity)} grouped by book,
 * used to build bestseller suggestions (Phase 02).
 */
public record BookSalesCount(Long bookId, Long soldQuantity) {
}
