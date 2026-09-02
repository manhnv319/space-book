package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;

public interface CreateBankTransferPaymentUseCase {
    BankTransferPaymentResponse create(Long orderId, Long userId);
}
