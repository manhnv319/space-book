package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;

import java.time.LocalDateTime;

/**
 * Một lần đơn hàng đổi trạng thái.
 *
 * `orders` chỉ giữ trạng thái hiện tại, nên nếu không ghi lại thì không dựng
 * được lộ trình có mốc giờ cho khách xem. `source` cho biết ai đổi — nhân viên,
 * hệ thống hay thanh toán — để sau này tích hợp đơn vị vận chuyển thật vẫn phân
 * biệt được nguồn.
 */
public record OrderStatusChange(Long id, Long orderId, OrderStatus status, String source, LocalDateTime changedAt) {
    public static final String SOURCE_PAYMENT = "PAYMENT";
    public static final String SOURCE_STAFF = "STAFF";
    public static final String SOURCE_AUTO = "AUTO";
}
