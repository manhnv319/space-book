package com.velstrong.bookstore.application.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.order.CancelOrderCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.port.in.order.CancelOrderUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.CancelVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;


@Service
@Transactional
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final CancelVoucherUseCase cancelVoucherUseCase;

    public CancelOrderService(OrderRepository orderRepository, CancelVoucherUseCase cancelVoucherUseCase) {
        this.orderRepository = orderRepository;
        this.cancelVoucherUseCase = cancelVoucherUseCase;
    }

    @Override
    public OrderResponse cancel(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", command.orderId()));

        if (!order.getUserId().equals(command.userId()))
            throw new InvalidOperationException("You are not authorized to cancel this order");

        order.cancel();
        Order saved = orderRepository.save(order);

        cancelVoucherUseCase.cancelReservation(command.orderId());

        return OrderResponse.from(saved);
    }
}
