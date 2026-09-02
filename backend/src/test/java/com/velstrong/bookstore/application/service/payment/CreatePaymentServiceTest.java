package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.CreatePaymentCommand;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.VNPayPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePaymentServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final VNPayPort vnPayPort = mock(VNPayPort.class);
    private final CreatePaymentService service = new CreatePaymentService(orderRepository, vnPayPort);

    @Test
    void rejectsPaymentUrlForAnotherUsersOrder() {
        Order order = Order.reconstitute(1L, 10L, "ORD-1", OrderType.PURCHASE,
                OrderStatus.CONFIRMED, PaymentStatus.UNPAID, PaymentMethod.VNPAY,
                1, 100_000L, 0L, 0L, null, 99L, null,
                LocalDateTime.now(), null, List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class,
                () -> service.createPaymentUrl(new CreatePaymentCommand(1L, 20L, "127.0.0.1")));

        verify(vnPayPort, never()).createPaymentUrl(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }
}
