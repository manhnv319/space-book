package com.velstrong.bookstore.application.response.subscription;

import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.model.enums.subscription.SubscriptionStatus;

public record SubscriptionResponse(
        Long id,
        String name,
        String description,
        Long price,
        Integer durationDays,
        Integer maxRentals,
        SubscriptionStatus status
) {
    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(s.getId(), s.getName(), s.getDescription(),
                s.getPrice(), s.getDurationDays(), s.getMaxRentals(), s.getStatus());
    }
}
