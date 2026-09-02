package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;

public interface CreateSubscriptionUseCase {
    SubscriptionResponse create(String name, String description, Long price,
                                Integer durationDays, Integer maxRentals);
}
