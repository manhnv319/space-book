package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;

public interface CreateOrderUseCase {
    OrderResponse create(CreateOrderCommand command);
}
