package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.command.order.CancelOrderCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;

public interface CancelOrderUseCase {
    OrderResponse cancel(CancelOrderCommand command);
}
