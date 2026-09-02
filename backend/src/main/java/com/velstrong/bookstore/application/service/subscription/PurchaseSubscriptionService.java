package com.velstrong.bookstore.application.service.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.subscription.PurchaseSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.in.subscription.PurchaseSubscriptionUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;

/**
 * Đặt mua một gói thuê tháng.
 *
 * Gói được tạo ở trạng thái chờ thanh toán kèm một khoản tiền phải trả, chứ
 * không kích hoạt ngay: trước đây bấm mua là dùng được luôn mà chưa trả đồng
 * nào. Gói chỉ sống khi bộ đối soát nhận được báo có khớp mã chuyển khoản.
 */
@Service
@Transactional
public class PurchaseSubscriptionService implements PurchaseSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final PaymentRepository payments;

    public PurchaseSubscriptionService(SubscriptionRepository subscriptionRepository,
                                       CustomerSubscriptionRepository customerSubscriptionRepository,
                                       PaymentRepository payments) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.payments = payments;
    }

    @Override
    public CustomerSubscriptionResponse purchase(PurchaseSubscriptionCommand command) {
        Subscription subscription = subscriptionRepository.findById(command.subscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Subscription", command.subscriptionId()));
        if (subscription.getPrice() == null || subscription.getPrice() <= 0) {
            throw new InvalidOperationException("Subscription has no price to charge");
        }

        CustomerSubscription pending = customerSubscriptionRepository.save(
                CustomerSubscription.createPendingPayment(command.userId(), subscription.getId()));
        pending.setSubscription(subscription);

        payments.save(Payment.createForSubscription(pending.getId(), subscription.getPrice(),
                PaymentMethod.BANK_TRANSFER));

        return CustomerSubscriptionResponse.from(pending);
    }
}
