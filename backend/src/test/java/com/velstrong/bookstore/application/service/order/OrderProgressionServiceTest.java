package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * The delivery route is simulated until a courier is integrated, so what matters
 * is that it walks one stage at a time, stops at the end, and leaves a trail the
 * customer's tracking page can render.
 */
class OrderProgressionServiceTest {

    private OrderRepository orders;
    private OrderStatusHistoryRepository history;
    private OrderProgressionService service;

    @BeforeEach
    void setUp() {
        orders = mock(OrderRepository.class);
        history = mock(OrderStatusHistoryRepository.class);
        when(orders.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));
        service = new OrderProgressionService(orders, history);
    }

    private Order orderAt(OrderStatus status) {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(7L);
        when(order.getStatus()).thenReturn(status);
        return order;
    }

    private void given(Order order) {
        when(orders.findAdvanceable(anyList(), any(LocalDateTime.class))).thenReturn(List.of(order));
    }

    @Test
    void movesAConfirmedOrderToProcessing() {
        Order order = orderAt(OrderStatus.CONFIRMED);
        given(order);

        assertThat(service.advanceOrdersOlderThan(LocalDateTime.now())).isEqualTo(1);
        verify(order).updateStatus(OrderStatus.PROCESSING);
    }

    @Test
    void movesShippingToCompleted() {
        Order order = orderAt(OrderStatus.SHIPPING);
        given(order);

        service.advanceOrdersOlderThan(LocalDateTime.now());

        verify(order).updateStatus(OrderStatus.COMPLETED);
    }

    @Test
    void leavesAnOrderThatHasArrivedAlone() {
        // COMPLETED has no next stage; nudging it further would invent a status.
        Order order = orderAt(OrderStatus.COMPLETED);
        given(order);

        assertThat(service.advanceOrdersOlderThan(LocalDateTime.now())).isZero();
        verify(order, never()).updateStatus(any());
        verifyNoInteractions(history);
    }

    @Test
    void recordsEveryStepSoTheTrackingPageHasTimestamps() {
        given(orderAt(OrderStatus.PROCESSING));

        service.advanceOrdersOlderThan(LocalDateTime.now());

        ArgumentCaptor<String> source = ArgumentCaptor.forClass(String.class);
        verify(history).record(eq(7L), eq(OrderStatus.SHIPPING), source.capture(), any(LocalDateTime.class));
        assertThat(source.getValue()).isEqualTo(OrderStatusChange.SOURCE_AUTO);
    }

    @Test
    void onlyAsksForStatusesThatHaveSomewhereToGo() {
        given(orderAt(OrderStatus.CONFIRMED));

        service.advanceOrdersOlderThan(LocalDateTime.now());

        ArgumentCaptor<List<OrderStatus>> asked = ArgumentCaptor.captor();
        verify(orders).findAdvanceable(asked.capture(), any(LocalDateTime.class));
        assertThat(asked.getValue())
                .containsExactlyInAnyOrder(OrderStatus.CONFIRMED, OrderStatus.PROCESSING, OrderStatus.SHIPPING)
                .doesNotContain(OrderStatus.COMPLETED, OrderStatus.CANCELLED);
    }
}
