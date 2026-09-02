package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;

/**
 * F26: returns {@code null} when the user has no active subscription
 * instead of {@code Optional} so the REST envelope is a clean
 * {@code ApiResponse<...>} with {@code data: null}.
 */
public interface GetActiveSubscriptionUseCase {
    CustomerSubscriptionResponse getActiveByUserId(Long userId);
}
