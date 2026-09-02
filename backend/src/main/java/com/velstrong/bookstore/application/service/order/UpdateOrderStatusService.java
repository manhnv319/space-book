package com.velstrong.bookstore.application.service.order;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.order.UpdateOrderStatusCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.port.in.order.UpdateOrderStatusUseCase;
import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;

import java.time.LocalDateTime;


@Service
@Transactional
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository history;
    private final NotificationUseCase notifications;

    public UpdateOrderStatusService(OrderRepository orderRepository, OrderStatusHistoryRepository history) {
        this(orderRepository, history, null);
    }

    @Autowired
    public UpdateOrderStatusService(OrderRepository orderRepository, OrderStatusHistoryRepository history, NotificationUseCase notifications) {
        this.orderRepository = orderRepository;
        this.history = history;
        this.notifications = notifications;
    }

    @Override
    public OrderResponse updateStatus(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", command.orderId()));
        order.updateStatus(command.newStatus());
        OrderResponse response = OrderResponse.from(orderRepository.save(order));
        history.record(order.getId(), command.newStatus(), OrderStatusChange.SOURCE_STAFF, LocalDateTime.now());
        if (notifications != null) notifications.notify(order.getUserId(), NotificationType.ORDER, "Cập nhật đơn hàng",
                "Đơn " + order.getOrderCode() + " đang ở trạng thái " + command.newStatus().name() + ".", "/don-hang/" + order.getId());
        return response;
    }
}
