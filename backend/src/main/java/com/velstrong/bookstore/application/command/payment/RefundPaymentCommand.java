package com.velstrong.bookstore.application.command.payment;

public record RefundPaymentCommand(Long orderId, Long userId, String reason) {}
