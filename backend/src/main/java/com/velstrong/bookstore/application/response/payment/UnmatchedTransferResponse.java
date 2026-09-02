package com.velstrong.bookstore.application.response.payment;

import com.velstrong.bookstore.domain.model.UnmatchedTransfer;

import java.time.LocalDateTime;

public record UnmatchedTransferResponse(
        Long id,
        String paymentReference,
        Long amount,
        LocalDateTime receivedAt,
        String reason,
        LocalDateTime createdAt
) {
    public static UnmatchedTransferResponse from(UnmatchedTransfer transfer) {
        return new UnmatchedTransferResponse(transfer.id(), transfer.paymentReference(), transfer.amount(),
                transfer.receivedAt(), transfer.reason(), transfer.createdAt());
    }
}
