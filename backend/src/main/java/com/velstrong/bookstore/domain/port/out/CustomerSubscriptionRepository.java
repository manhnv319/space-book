package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.PageResult;

import java.util.Optional;

public interface CustomerSubscriptionRepository {
    CustomerSubscription save(CustomerSubscription customerSubscription);
    Optional<CustomerSubscription> findById(Long id);
    Optional<CustomerSubscription> findActiveByUserId(Long userId);
    PageResult<CustomerSubscription> findByUserId(Long userId, int page, int size);
}
