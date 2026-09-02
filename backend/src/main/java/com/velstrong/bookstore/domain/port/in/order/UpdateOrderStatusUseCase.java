package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.command.order.UpdateOrderStatusCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;

public interface UpdateOrderStatusUseCase {
    OrderResponse updateStatus(UpdateOrderStatusCommand command);
}
