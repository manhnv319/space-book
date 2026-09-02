package com.velstrong.bookstore.application.command.payment;

public record CreatePaymentCommand(Long orderId, Long userId, String ipAddress) {}
