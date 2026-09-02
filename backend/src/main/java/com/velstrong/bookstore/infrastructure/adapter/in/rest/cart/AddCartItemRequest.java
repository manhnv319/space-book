package com.velstrong.bookstore.infrastructure.adapter.in.rest.cart;

import com.velstrong.bookstore.application.command.cart.AddCartItemCommand;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull Long bookId,
        @NotNull ItemType itemType,
        @Min(1) @Max(99) Integer quantity,
        Integer rentalTermValue,
        String rentalTermUnit
) {
    public AddCartItemCommand toCommand(Long userId) {
        return new AddCartItemCommand(userId, bookId, itemType, quantity, rentalTermValue, rentalTermUnit);
    }
}
