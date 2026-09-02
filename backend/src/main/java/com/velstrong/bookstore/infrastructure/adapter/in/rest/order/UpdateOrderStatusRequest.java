package com.velstrong.bookstore.infrastructure.adapter.in.rest.order;

import com.velstrong.bookstore.application.command.order.UpdateOrderStatusCommand;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * F22: dedicated request DTO so the controller does not bind the
 * command record directly — keeps validation concerns here and
 * command construction in one place.
 */
public record UpdateOrderStatusRequest(@NotNull OrderStatus newStatus) {

    public UpdateOrderStatusCommand toCommand(Long orderId) {
        return new UpdateOrderStatusCommand(orderId, newStatus);
    }
}
