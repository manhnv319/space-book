package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;
import com.velstrong.bookstore.domain.port.in.subscription.GetActiveSubscriptionsUseCase;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetActiveSubscriptionsService implements GetActiveSubscriptionsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public GetActiveSubscriptionsService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<SubscriptionResponse> getActive() {
        return subscriptionRepository.findAllActive().stream()
                .map(SubscriptionResponse::from)
                .toList();
    }
}
