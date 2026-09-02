package com.velstrong.bookstore.application.command.order;

public record CancelOrderCommand(Long orderId, Long userId) {}
