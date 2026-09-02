package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.application.service.payment.BankTransferInitializer;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.port.in.subscription.GetSubscriptionPaymentUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.QrImagePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Thông tin chuyển khoản cho một gói thuê tháng.
 *
 * Dùng chung `BankTransferInitializer` với đơn hàng, nên mã chuyển khoản của gói
 * và của đơn được sinh theo đúng một quy tắc và bộ đối soát nhận ra cả hai.
 */
@Service
@Transactional
public class GetSubscriptionPaymentService implements GetSubscriptionPaymentUseCase {

    private final CustomerSubscriptionRepository subscriptions;
    private final PaymentRepository payments;
    private final BankTransferInitializer transfers;
    private final QrImagePort qrImages;

    public GetSubscriptionPaymentService(CustomerSubscriptionRepository subscriptions, PaymentRepository payments,
                                         BankTransferInitializer transfers, QrImagePort qrImages) {
        this.subscriptions = subscriptions;
        this.payments = payments;
        this.transfers = transfers;
        this.qrImages = qrImages;
    }

    @Override
    public BankTransferPaymentResponse getPayment(Long customerSubscriptionId, Long userId) {
        transfers.requireConfigured();
        CustomerSubscription subscription = subscriptions.findById(customerSubscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription", customerSubscriptionId));
        if (!subscription.getUserId().equals(userId))
            throw new InvalidOperationException("Subscription does not belong to current user");

        Payment payment = payments.findByCustomerSubscriptionId(customerSubscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment for subscription", customerSubscriptionId));
        return transfers.initialize(payment, "TG", customerSubscriptionId);
    }

    @Override
    public byte[] renderQr(Long customerSubscriptionId, Long userId, int size) {
        return qrImages.renderPng(getPayment(customerSubscriptionId, userId).qrPayload(), size);
    }
}
