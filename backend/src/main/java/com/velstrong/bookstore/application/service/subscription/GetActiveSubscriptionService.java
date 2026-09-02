package com.velstrong.bookstore.application.service.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.port.in.subscription.GetActiveSubscriptionUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetActiveSubscriptionService implements GetActiveSubscriptionUseCase {

    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    public GetActiveSubscriptionService(CustomerSubscriptionRepository customerSubscriptionRepository,
                                         SubscriptionRepository subscriptionRepository, Clock clock) {
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
    }

    @Override
    public CustomerSubscriptionResponse getActiveByUserId(Long userId) {
        LocalDate today = LocalDate.now(clock);
        Optional<CustomerSubscription> active = customerSubscriptionRepository.findActiveByUserId(userId)
                .filter(cs -> cs.isActive(today));
        return active.map(cs -> {
            subscriptionRepository.findById(cs.getSubscriptionId())
                    .ifPresent(cs::setSubscription);
            return CustomerSubscriptionResponse.from(cs);
        }).orElse(null);
    }
}
