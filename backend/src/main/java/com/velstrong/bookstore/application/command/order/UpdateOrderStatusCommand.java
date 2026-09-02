package com.velstrong.bookstore.application.command.order;

import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;

public record UpdateOrderStatusCommand(Long orderId, OrderStatus newStatus) {}
