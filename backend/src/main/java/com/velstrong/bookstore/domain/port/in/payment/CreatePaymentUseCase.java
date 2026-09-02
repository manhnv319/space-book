package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.command.payment.CreatePaymentCommand;

public interface CreatePaymentUseCase {
    String createPaymentUrl(CreatePaymentCommand command);
}
