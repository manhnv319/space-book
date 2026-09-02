package com.velstrong.bookstore.infrastructure.adapter.in.rest.order;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty List<@Valid ItemRequest> items,
        @NotNull PaymentMethod paymentMethod,
        @NotNull Long shippingAddressId,
        String voucherCode,
        String notes
) {
    // NOTE: prices are computed server-side from Book (Validation S1) — any client-sent price is ignored.
    public record ItemRequest(
            @NotNull Long bookId,
            Long bookCopyId,
            @NotNull ItemType itemType,
            Integer quantity,
            Integer rentalTermValue,
            String rentalTermUnit
    ) {}

    public CreateOrderCommand toCommand(Long userId) {
        List<CreateOrderCommand.Item> commandItems = items.stream()
                .map(i -> new CreateOrderCommand.Item(i.bookId(), i.bookCopyId(), i.itemType(),
                        i.quantity(), i.rentalTermValue(), i.rentalTermUnit()))
                .toList();
        return new CreateOrderCommand(userId, commandItems, paymentMethod, shippingAddressId, voucherCode, notes);
    }
}
