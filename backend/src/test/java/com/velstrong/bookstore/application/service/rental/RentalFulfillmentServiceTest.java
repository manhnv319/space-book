package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.in.rental.StartRentalUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RentalFulfillmentServiceTest {

    @Test
    void allowsInternalRetryForAPaidRentalOrder() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StartRentalUseCase startRentalUseCase = mock(StartRentalUseCase.class);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(PaymentStatus.PAID, OrderType.RENTAL)));
        RentalFulfillmentService service = new RentalFulfillmentService(orderRepository, startRentalUseCase);

        service.fulfillPaidOrder(1L);
        service.fulfillPaidOrder(1L);

        verify(startRentalUseCase, times(2)).startFromOrder(1L);
    }

    @Test
    void refusesToFulfillAnUnpaidOrder() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(PaymentStatus.UNPAID, OrderType.RENTAL)));
        RentalFulfillmentService service = new RentalFulfillmentService(orderRepository, mock(StartRentalUseCase.class));

        assertThatThrownBy(() -> service.fulfillPaidOrder(1L)).isInstanceOf(InvalidOperationException.class);
    }

    private static Order order(PaymentStatus status, OrderType type) {
        return Order.reconstitute(1L, 1L, "ORD-1", type, OrderStatus.CONFIRMED, status, PaymentMethod.VNPAY,
                1, 1L, 0L, 0L, null, 1L, null, LocalDateTime.now(), null, List.of());
    }
}
