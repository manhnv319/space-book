package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.response.order.OrderDetailResponse;

public interface GetOrderUseCase {
    OrderDetailResponse getById(Long orderId, Long currentUserId);
    OrderDetailResponse getForManagement(Long orderId);
}
