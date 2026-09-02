package com.velstrong.bookstore.domain.model;

import java.time.LocalDateTime;

/**
 * A credit that arrived in the bank account but could not be attached to an order.
 *
 * Deliberately carries no transfer description: for a personal transaction that
 * happens to land in the same account, the description is private information
 * that has no place in this database. Amount and time are enough for a human to
 * find the transaction in their banking app.
 */
public record UnmatchedTransfer(
        Long id,
        String paymentReference,
        Long amount,
        LocalDateTime receivedAt,
        String reason,
        LocalDateTime createdAt
) {}
