package com.velstrong.bookstore.application.command.cart;

public record DeleteCartItemCommand(Long userId, Long cartItemId) {}
