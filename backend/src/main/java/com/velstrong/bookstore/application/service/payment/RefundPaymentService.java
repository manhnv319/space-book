package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.RefundPaymentCommand;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.payment.RefundPaymentUseCase;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RefundPaymentService implements RefundPaymentUseCase {

    private final PaymentRepository paymentRepository;

    public RefundPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse refund(RefundPaymentCommand command) {
        var payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Payment for order", command.orderId()));
        payment.markRefunded();
        return PaymentResponse.from(paymentRepository.save(payment));
    }
}
