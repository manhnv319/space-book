package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;

public interface GetMySubscriptionsUseCase {
    PagedResponse<CustomerSubscriptionResponse> getMySubscriptions(Long userId, int page, int size);
}
