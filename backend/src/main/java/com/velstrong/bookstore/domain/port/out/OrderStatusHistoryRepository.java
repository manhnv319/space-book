package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderStatusHistoryRepository {
    void record(Long orderId, OrderStatus status, String source, LocalDateTime changedAt);

    /** Cũ nhất trước — lộ trình đọc từ trên xuống. */
    List<OrderStatusChange> findByOrderId(Long orderId);
}
