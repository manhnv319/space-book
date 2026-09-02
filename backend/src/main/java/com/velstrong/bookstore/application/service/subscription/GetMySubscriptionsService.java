package com.velstrong.bookstore.application.service.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.port.in.subscription.GetMySubscriptionsUseCase;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;

@Service
@Transactional(readOnly = true)
public class GetMySubscriptionsService implements GetMySubscriptionsUseCase {

    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;

    public GetMySubscriptionsService(CustomerSubscriptionRepository customerSubscriptionRepository,
                                      SubscriptionRepository subscriptionRepository) {
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public PagedResponse<CustomerSubscriptionResponse> getMySubscriptions(Long userId, int page, int size) {
        var result = customerSubscriptionRepository.findByUserId(userId, page, size);

        var subIds = result.content().stream()
                .map(CustomerSubscription::getSubscriptionId)
                .collect(java.util.stream.Collectors.toSet());
        var subMap = subscriptionRepository.findByIds(subIds).stream()
                .collect(java.util.stream.Collectors.toMap(Subscription::getId, s -> s));

        var responses = result.content().stream()
                .map(cs -> {
                    var sub = subMap.get(cs.getSubscriptionId());
                    if (sub != null) cs.setSubscription(sub);
                    return CustomerSubscriptionResponse.from(cs);
                })
                .toList();

        return PagedResponse.of(responses, page, size, result.totalElements());
    }
}
