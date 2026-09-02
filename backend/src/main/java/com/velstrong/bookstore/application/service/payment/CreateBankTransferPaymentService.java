package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.in.payment.CreateBankTransferPaymentUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateBankTransferPaymentService implements CreateBankTransferPaymentUseCase {

    private final OrderRepository orders;
    private final PaymentRepository payments;
    private final BankTransferInitializer transfers;

    public CreateBankTransferPaymentService(OrderRepository orders, PaymentRepository payments,
                                            BankTransferInitializer transfers) {
        this.orders = orders;
        this.payments = payments;
        this.transfers = transfers;
    }

    @Override
    public BankTransferPaymentResponse create(Long orderId, Long userId) {
        transfers.requireConfigured();
        Order order = orders.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (!order.getUserId().equals(userId))
            throw new InvalidOperationException("Order does not belong to current user");
        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER)
            throw new InvalidOperationException("Order does not use bank transfer");

        Payment payment = payments.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment for order", orderId));
        return transfers.initialize(payment, "TQ", orderId);
    }
}
