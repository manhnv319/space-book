package com.velstrong.bookstore.application.response.payment;

import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentTransactionStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        Long amount,
        PaymentMethod method,
        PaymentTransactionStatus status,
        String transactionId,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getOrderId(), payment.getAmount(),
                payment.getMethod(), payment.getStatus(), payment.getTransactionId(),
                payment.getPaidAt(), payment.getCreatedAt()
        );
    }
}
