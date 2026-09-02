package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;
import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.port.in.subscription.CreateSubscriptionUseCase;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSubscriptionService implements CreateSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public CreateSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SubscriptionResponse create(String name, String description, Long price,
                                       Integer durationDays, Integer maxRentals) {
        Subscription subscription = Subscription.create(name, description, price, durationDays, maxRentals);
        return SubscriptionResponse.from(subscriptionRepository.save(subscription));
    }
}
