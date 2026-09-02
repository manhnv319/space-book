package com.velstrong.bookstore.application.response.order;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderCode,
        OrderType orderType,
        OrderStatus status,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        Integer totalItems,
        Long totalAmount,
        Long totalDeposit,
        Long totalDiscount,
        Long finalAmount,
        LocalDateTime createdAt,
        /**
         * Vài sản phẩm đầu để hiển thị ngay trên danh sách đơn.
         *
         * Danh sách chỉ có mã đơn thì khách không nhận ra đơn nào là đơn nào — mã
         * đơn là ULID, đọc không ra nghĩa gì. Rỗng với đơn không còn sản phẩm nào
         * tra được.
         */
        List<ItemPreview> items
) {
    public record ItemPreview(Long bookId, String title, String imageUrl, ItemType itemType, Integer quantity) {}

    public static OrderResponse from(Order order) {
        return from(order, List.of());
    }

    public static OrderResponse from(Order order, List<ItemPreview> items) {
        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getOrderType(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getTotalItems(),
                order.getTotalAmount(),
                order.getTotalDeposit(),
                order.getTotalDiscount(),
                order.getFinalAmount(),
                order.getCreatedAt(),
                items
        );
    }
}
