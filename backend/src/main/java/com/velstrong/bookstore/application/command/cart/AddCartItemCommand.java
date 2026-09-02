package com.velstrong.bookstore.application.command.cart;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;

public record AddCartItemCommand(
        Long userId,
        Long bookId,
        ItemType itemType,
        Integer quantity,
        Integer rentalTermValue,
        String rentalTermUnit
) {}
