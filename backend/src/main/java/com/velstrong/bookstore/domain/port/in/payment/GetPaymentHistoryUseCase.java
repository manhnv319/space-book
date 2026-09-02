package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;

public interface GetPaymentHistoryUseCase {
    PagedResponse<PaymentResponse> getByUserId(Long userId, int page, int size);
}
