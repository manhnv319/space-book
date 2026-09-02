package com.velstrong.bookstore.application.command.payment;

import java.time.LocalDateTime;

public record BankTransferNotification(
        String messageId,
        String transactionReference,
        String paymentReference,
        long amount,
        LocalDateTime occurredAt
) {}
