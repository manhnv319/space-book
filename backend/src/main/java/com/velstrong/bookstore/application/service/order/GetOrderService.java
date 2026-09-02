package com.velstrong.bookstore.application.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.order.OrderDetailResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.port.in.order.GetOrderUseCase;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistory;

    public GetOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                           OrderStatusHistoryRepository statusHistory) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistory = statusHistory;
    }

    @Override
    public OrderDetailResponse getById(Long orderId, Long currentUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (!order.getUserId().equals(currentUserId)) {
            throw new InvalidOperationException("Order does not belong to current user");
        }
        return toResponse(order);
    }

    @Override
    public OrderDetailResponse getForManagement(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        return toResponse(order);
    }

    private OrderDetailResponse toResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        order.setItems(items);
        return OrderDetailResponse.from(order, statusHistory.findByOrderId(order.getId()));
    }
}
