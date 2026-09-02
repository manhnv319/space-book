package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("create returns a CONFIRMED/UNPAID order with no items")
    void createBuildsFreshOrder() {
        Order o = Order.create(7L, "ORD-1", OrderType.PURCHASE,
                PaymentMethod.VNPAY, 99L, "note");

        assertThat(o.getId()).isNull();
        assertThat(o.getUserId()).isEqualTo(7L);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(o.getPaymentStatus()).isEqualTo(PaymentStatus.UNPAID);
        assertThat(o.getItems()).isEmpty();
    }

    @Test
    @DisplayName("calculateTotals sums item subtotals, quantities, and rental deposits")
    void calculateTotalsSumsCorrectly() {
        Order o = Order.create(1L, "ORD-1", OrderType.MIXED, PaymentMethod.VNPAY, 1L, null);
        o.setItems(List.of(
                OrderItem.createPurchase(10L, 2, 50_000L),
                OrderItem.createPurchase(11L, 1, 30_000L),
                OrderItem.createRental(12L, 100L, 20_000L, 50_000L)
        ));

        o.calculateTotals();

        assertThat(o.getTotalItems()).isEqualTo(4);
        assertThat(o.getTotalAmount()).isEqualTo(150_000L);
        assertThat(o.getTotalDeposit()).isEqualTo(50_000L);
    }

    @Test
    @DisplayName("calculateTotals handles empty items without throwing")
    void calculateTotalsEmpty() {
        Order o = Order.create(1L, "ORD-1", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o.calculateTotals();
        assertThat(o.getTotalItems()).isEqualTo(0);
        assertThat(o.getTotalAmount()).isEqualTo(0L);
        assertThat(o.getTotalDeposit()).isEqualTo(0L);
    }

    @Test
    @DisplayName("applyDiscount only sets when amount is positive")
    void applyDiscountPositiveOnly() {
        Order o = Order.create(1L, "ORD-1", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o.applyDiscount(null);
        assertThat(o.getTotalDiscount()).isEqualTo(0L);
        o.applyDiscount(0L);
        assertThat(o.getTotalDiscount()).isEqualTo(0L);
        o.applyDiscount(15_000L);
        assertThat(o.getTotalDiscount()).isEqualTo(15_000L);
    }

    @Test
    @DisplayName("getFinalAmount: totalAmount - discount + deposit, never below zero")
    void getFinalAmount() {
        Order o = Order.create(1L, "ORD-1", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o.setItems(List.of(OrderItem.createPurchase(10L, 1, 100_000L)));
        o.calculateTotals();
        o.applyDiscount(20_000L);
        assertThat(o.getFinalAmount()).isEqualTo(80_000L);

        Order o2 = Order.create(1L, "ORD-2", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o2.setItems(List.of(OrderItem.createPurchase(10L, 1, 100_000L)));
        o2.calculateTotals();
        o2.applyDiscount(500_000L);
        assertThat(o2.getFinalAmount()).isEqualTo(0L);
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        void cancelsPendingOrder() {
            Order o = Order.reconstitute(1L, 7L, "ORD-1", OrderType.PURCHASE,
                    OrderStatus.PENDING, PaymentStatus.UNPAID, PaymentMethod.VNPAY,
                    0, 0L, 0L, 0L, null, null, null, null, null, List.of());
            o.cancel();
            assertThat(o.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void rejectsCancelOnShippedOrder() {
            Order o = Order.reconstitute(1L, 7L, "ORD-1", OrderType.PURCHASE,
                    OrderStatus.SHIPPING, PaymentStatus.PAID, PaymentMethod.VNPAY,
                    0, 0L, 0L, 0L, null, null, null, null, null, List.of());

            assertThatThrownBy(o::cancel)
                    .isInstanceOf(InvalidOperationException.class);
        }
    }

    @Test
    @DisplayName("markPaid flips paymentStatus to PAID and stamps modifiedAt")
    void markPaid() {
        Order o = Order.create(1L, "ORD-1", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o.markPaid();
        assertThat(o.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(o.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateStatus replaces status")
    void updateStatus() {
        Order o = Order.create(1L, "ORD-1", OrderType.PURCHASE, PaymentMethod.VNPAY, 1L, null);
        o.updateStatus(OrderStatus.SHIPPING);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    @DisplayName("OrderItem.createPurchase computes subtotal = quantity * unitPrice")
    void orderItemCreatePurchase() {
        OrderItem i = OrderItem.createPurchase(10L, 3, 25_000L);
        assertThat(i.getItemType()).isEqualTo(ItemType.PURCHASE);
        assertThat(i.getSubtotal()).isEqualTo(75_000L);
    }

    @Test
    @DisplayName("OrderItem.createRental uses unitPrice as subtotal and stores deposit")
    void orderItemCreateRental() {
        OrderItem i = OrderItem.createRental(10L, 100L, 20_000L, 50_000L);
        assertThat(i.getItemType()).isEqualTo(ItemType.RENTAL);
        assertThat(i.getSubtotal()).isEqualTo(20_000L);
        assertThat(i.getDepositAmount()).isEqualTo(50_000L);
        assertThat(i.getQuantity()).isEqualTo(1);
    }
}
