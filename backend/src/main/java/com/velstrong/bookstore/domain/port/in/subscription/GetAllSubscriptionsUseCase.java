package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;

import java.util.List;

public interface GetAllSubscriptionsUseCase {
    List<SubscriptionResponse> getAll();
}
