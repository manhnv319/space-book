package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByCustomerSubscriptionId(Long customerSubscriptionId);
    Optional<Payment> findByTransferReference(String transferReference);
    List<Payment> findExpiredPendingBankTransfers();
    List<Payment> findAllByOrderId(Long orderId);
    PageResult<Payment> findByUserId(Long userId, int page, int size);
}
