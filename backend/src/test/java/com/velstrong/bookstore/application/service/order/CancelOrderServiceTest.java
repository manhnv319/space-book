package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.command.order.CancelOrderCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.in.voucher.CancelVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelOrderServiceTest {

    private OrderRepository orderRepository;
    private CancelVoucherUseCase cancelVoucherUseCase;
    private CancelOrderService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        cancelVoucherUseCase = mock(CancelVoucherUseCase.class);
        service = new CancelOrderService(orderRepository, cancelVoucherUseCase);
    }

    @Test
    @DisplayName("cancels the order, saves, then cancels voucher reservation")
    void cancelsAndCancelsVoucher() {
        Order order = Order.reconstitute(1L, 7L, "ORD-1", OrderType.PURCHASE,
                OrderStatus.PENDING, PaymentStatus.UNPAID, PaymentMethod.VNPAY,
                1, 50_000L, 0L, 0L, 5L, 99L, null,
                LocalDateTime.now(), null, List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var response = service.cancel(new CancelOrderCommand(1L, 7L));

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        InOrder inOrder = inOrder(orderRepository, cancelVoucherUseCase);
        inOrder.verify(orderRepository).save(order);
        inOrder.verify(cancelVoucherUseCase).cancelReservation(1L);
    }

    @Test
    @DisplayName("rejects when order does not exist")
    void rejectsMissingOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(new CancelOrderCommand(1L, 7L)))
                .isInstanceOf(EntityNotFoundException.class);
        verify(cancelVoucherUseCase, never()).cancelReservation(1L);
    }

    @Test
    @DisplayName("rejects when caller is not the order owner")
    void rejectsForeignUser() {
        Order order = Order.reconstitute(1L, 7L, "ORD-1", OrderType.PURCHASE,
                OrderStatus.PENDING, PaymentStatus.UNPAID, PaymentMethod.VNPAY,
                1, 50_000L, 0L, 0L, null, 99L, null,
                LocalDateTime.now(), null, List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.cancel(new CancelOrderCommand(1L, 99L)))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("not authorized");
        verify(orderRepository, never()).save(order);
    }

    @Test
    @DisplayName("rejects cancel on shipped order")
    void rejectsCancelOnShippedOrder() {
        Order order = Order.reconstitute(1L, 7L, "ORD-1", OrderType.PURCHASE,
                OrderStatus.SHIPPING, PaymentStatus.PAID, PaymentMethod.VNPAY,
                1, 50_000L, 0L, 0L, null, 99L, null,
                LocalDateTime.now(), null, List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.cancel(new CancelOrderCommand(1L, 7L)))
                .isInstanceOf(InvalidOperationException.class);
    }
}
