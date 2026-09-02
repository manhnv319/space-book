package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;

public interface GetSubscriptionPaymentUseCase {
    /** Thông tin chuyển khoản cho gói của chính người gọi; sinh mã ở lần gọi đầu. */
    BankTransferPaymentResponse getPayment(Long customerSubscriptionId, Long userId);

    byte[] renderQr(Long customerSubscriptionId, Long userId, int size);
}
