package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private final Long id;
    private final Long userId;
    private final String orderCode;
    private final OrderType orderType;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private final PaymentMethod paymentMethod;
    private Integer totalItems;
    private Long totalAmount;
    private Long totalDeposit;
    private Long totalDiscount;
    private Long voucherId;
    private final Long shippingAddressId;
    private final String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<OrderItem> items;

    private Order(Long id, Long userId, String orderCode, OrderType orderType,
                  OrderStatus status, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
                  Integer totalItems, Long totalAmount, Long totalDeposit, Long totalDiscount,
                  Long voucherId, Long shippingAddressId, String notes,
                  LocalDateTime createdAt, LocalDateTime modifiedAt, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.orderCode = orderCode;
        this.orderType = orderType;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.totalDeposit = totalDeposit;
        this.totalDiscount = totalDiscount;
        this.voucherId = voucherId;
        this.shippingAddressId = shippingAddressId;
        this.notes = notes;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.items = items != null ? items : new ArrayList<>();
    }

    public static Order create(Long userId, String orderCode, OrderType orderType,
                               PaymentMethod paymentMethod, Long shippingAddressId, String notes) {
        return new Order(null, userId, orderCode, orderType,
                OrderStatus.CONFIRMED, PaymentStatus.UNPAID, paymentMethod,
                0, 0L, 0L, 0L, null, shippingAddressId, notes,
                LocalDateTime.now(), null, new ArrayList<>());
    }

    public static Order reconstitute(Long id, Long userId, String orderCode, OrderType orderType,
                                     OrderStatus status, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
                                     Integer totalItems, Long totalAmount, Long totalDeposit, Long totalDiscount,
                                     Long voucherId, Long shippingAddressId, String notes,
                                     LocalDateTime createdAt, LocalDateTime modifiedAt, List<OrderItem> items) {
        return new Order(id, userId, orderCode, orderType, status, paymentStatus, paymentMethod,
                totalItems, totalAmount, totalDeposit, totalDiscount, voucherId,
                shippingAddressId, notes, createdAt, modifiedAt, items);
    }

    public void calculateTotals() {
        if (items == null || items.isEmpty()) {
            this.totalItems = 0;
            this.totalAmount = 0L;
            this.totalDeposit = 0L;
            return;
        }
        this.totalItems = items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
        this.totalAmount = items.stream().mapToLong(i -> i.getSubtotal() != null ? i.getSubtotal() : 0L).sum();
        this.totalDeposit = items.stream()
                .filter(OrderItem::isRental)
                .mapToLong(i -> i.getDepositAmount() != null ? i.getDepositAmount() : 0L)
                .sum();
    }

    public void applyDiscount(Long discountAmount) {
        if (discountAmount != null && discountAmount > 0) this.totalDiscount = discountAmount;
    }

    public Long getFinalAmount() {
        long subtotal = totalAmount != null ? totalAmount : 0L;
        long discount = totalDiscount != null ? totalDiscount : 0L;
        long deposit = totalDeposit != null ? totalDeposit : 0L;
        long finalAmt = subtotal - discount + deposit;
        return finalAmt > 0 ? finalAmt : 0L;
    }

    public void cancel() {
        if (!status.canBeCancelled())
            throw new InvalidOperationException("Cannot cancel order with status: " + status);
        this.status = OrderStatus.CANCELLED;
        this.modifiedAt = LocalDateTime.now();
    }

    public void markPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.modifiedAt = LocalDateTime.now();
    }

    public boolean canBeCancelled() { return status != null && status.canBeCancelled(); }
    public boolean isPurchaseOrder() { return orderType != null && orderType.isPurchase(); }
    public boolean isRentalOrder() { return orderType != null && orderType.isRental(); }
    public boolean isMixedOrder() { return orderType != null && orderType.isMixed(); }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getOrderCode() { return orderCode; }
    public OrderType getOrderType() { return orderType; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Integer getTotalItems() { return totalItems; }
    public Long getTotalAmount() { return totalAmount; }
    public Long getTotalDeposit() { return totalDeposit; }
    public Long getTotalDiscount() { return totalDiscount; }
    public Long getVoucherId() { return voucherId; }
    public Long getShippingAddressId() { return shippingAddressId; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setVoucherId(Long voucherId) { this.voucherId = voucherId; }
    public void setTotalDiscount(Long totalDiscount) { this.totalDiscount = totalDiscount; }
}
