package com.velstrong.bookstore.application.command.order;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;

import java.util.List;

public record CreateOrderCommand(
        Long userId,
        List<Item> items,
        PaymentMethod paymentMethod,
        Long shippingAddressId,
        String voucherCode,
        String notes
) {
    public record Item(
            Long bookId,
            Long bookCopyId,
            ItemType itemType,
            Integer quantity,
            Integer rentalTermValue,
            String rentalTermUnit
    ) {}
}
