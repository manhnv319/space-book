package com.velstrong.bookstore.application.response.order;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
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
        Long shippingAddressId,
        String notes,
        LocalDateTime createdAt,
        List<ItemDetail> items,
        /** Cũ nhất trước. Rỗng với đơn tạo trước khi có bảng lịch sử. */
        List<StatusStep> timeline
) {
    public record StatusStep(OrderStatus status, String source, LocalDateTime changedAt) {
        public static StatusStep from(com.velstrong.bookstore.domain.model.OrderStatusChange change) {
            return new StatusStep(change.status(), change.source(), change.changedAt());
        }
    }

    public record ItemDetail(Long bookId, Long bookCopyId, ItemType itemType, Integer quantity, Long unitPrice,
                             Long depositAmount, Integer rentalTermValue, RentalTermUnit rentalTermUnit,
                             Long subtotal) {
        public static ItemDetail from(OrderItem item) {
            return new ItemDetail(item.getBookId(), item.getBookCopyId(), item.getItemType(),
                    item.getQuantity(), item.getUnitPrice(), item.getDepositAmount(), item.getRentalTermValue(),
                    item.getRentalTermUnit(), item.getSubtotal());
        }
    }

    public static OrderDetailResponse from(Order order) {
        return from(order, List.of());
    }

    public static OrderDetailResponse from(Order order,
            List<com.velstrong.bookstore.domain.model.OrderStatusChange> history) {
        List<ItemDetail> items = order.getItems() != null
                ? order.getItems().stream().map(ItemDetail::from).toList()
                : List.of();
        return new OrderDetailResponse(
                order.getId(), order.getOrderCode(), order.getOrderType(),
                order.getStatus(), order.getPaymentStatus(), order.getPaymentMethod(),
                order.getTotalItems(), order.getTotalAmount(), order.getTotalDeposit(),
                order.getTotalDiscount(), order.getFinalAmount(),
                order.getShippingAddressId(), order.getNotes(), order.getCreatedAt(), items,
                history.stream().map(StatusStep::from).toList()
        );
    }
}
