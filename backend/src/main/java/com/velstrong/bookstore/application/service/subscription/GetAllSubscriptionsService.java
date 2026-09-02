package com.velstrong.bookstore.application.service.subscription;

import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;
import com.velstrong.bookstore.domain.port.in.subscription.GetAllSubscriptionsUseCase;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllSubscriptionsService implements GetAllSubscriptionsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public GetAllSubscriptionsService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<SubscriptionResponse> getAll() {
        return subscriptionRepository.findAll().stream()
                .map(SubscriptionResponse::from)
                .toList();
    }
}
