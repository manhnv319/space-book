package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.CreatePaymentCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.port.in.payment.CreatePaymentUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.VNPayPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CreatePaymentService implements CreatePaymentUseCase {

    private final OrderRepository orderRepository;
    private final VNPayPort vnPayPort;

    public CreatePaymentService(OrderRepository orderRepository, VNPayPort vnPayPort) {
        this.orderRepository = orderRepository;
        this.vnPayPort = vnPayPort;
    }

    @Override
    public String createPaymentUrl(CreatePaymentCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order", command.orderId()));
        if (!order.getUserId().equals(command.userId())) {
            throw new InvalidOperationException("Order does not belong to current user");
        }
        return vnPayPort.createPaymentUrl(order.getOrderCode(), order.getFinalAmount(),
                "Payment for " + order.getOrderCode(), command.ipAddress());
    }
}
