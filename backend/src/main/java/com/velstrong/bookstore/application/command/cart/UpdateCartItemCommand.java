package com.velstrong.bookstore.application.command.cart;

public record UpdateCartItemCommand(Long userId, Long cartItemId, Integer quantity) {}
