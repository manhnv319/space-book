package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.command.payment.ConfirmPaymentCommand;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;

public interface ConfirmPaymentUseCase {
    PaymentResponse confirm(ConfirmPaymentCommand command);
}
