package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.BookSalesCount;
import com.velstrong.bookstore.domain.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    List<OrderItem> saveAll(List<OrderItem> items);
    List<OrderItem> findByOrderId(Long orderId);
    List<BookSalesCount> findTopSellingBooks(LocalDateTime since, String itemType, int limit);
}
