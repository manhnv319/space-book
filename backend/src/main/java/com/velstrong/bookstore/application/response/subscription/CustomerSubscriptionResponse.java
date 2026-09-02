package com.velstrong.bookstore.application.response.subscription;

import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;

import java.time.LocalDate;

public record CustomerSubscriptionResponse(
        Long id,
        Long userId,
        SubscriptionResponse subscription,
        LocalDate startDate,
        LocalDate endDate,
        Integer usedRentals,
        CustomerSubscriptionStatus status
) {
    public static CustomerSubscriptionResponse from(CustomerSubscription cs) {
        SubscriptionResponse sub = cs.getSubscription() != null
                ? SubscriptionResponse.from(cs.getSubscription()) : null;
        return new CustomerSubscriptionResponse(cs.getId(), cs.getUserId(), sub,
                cs.getStartDate(), cs.getEndDate(), cs.getUsedRentals(), cs.getStatus());
    }
}
