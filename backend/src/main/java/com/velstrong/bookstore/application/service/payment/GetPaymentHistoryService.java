package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;
import com.velstrong.bookstore.domain.port.in.payment.GetPaymentHistoryUseCase;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetPaymentHistoryService implements GetPaymentHistoryUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentHistoryService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PagedResponse<PaymentResponse> getByUserId(Long userId, int page, int size) {
        var result = paymentRepository.findByUserId(userId, page, size);
        return PagedResponse.of(
                result.content().stream().map(PaymentResponse::from).toList(),
                page, size, result.totalElements());
    }
}
