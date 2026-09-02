package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;

public interface GetMyOrdersUseCase {
    PagedResponse<OrderResponse> getMyOrders(Long userId, OrderStatus status, PaymentStatus paymentStatus, int page, int size);

    /** Lọc theo nhiều trạng thái, cho tab gộp ở giao diện khách. */
    com.velstrong.bookstore.application.response.common.PagedResponse<
            com.velstrong.bookstore.application.response.order.OrderResponse> getMyOrdersByStatuses(
            Long userId, java.util.List<com.velstrong.bookstore.domain.model.enums.order.OrderStatus> statuses,
            int page, int size);

    /** Số đơn theo trạng thái, cho số đếm trên tab. */
    java.util.Map<com.velstrong.bookstore.domain.model.enums.order.OrderStatus, Long> countMyOrdersByStatus(Long userId);
}
