package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.command.payment.RefundPaymentCommand;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;

public interface RefundPaymentUseCase {
    PaymentResponse refund(RefundPaymentCommand command);
}
