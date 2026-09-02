package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PushSubscription;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository {
    PushSubscription save(PushSubscription subscription);
    List<PushSubscription> findByUserId(Long userId);
    Optional<PushSubscription> findByUserIdAndEndpoint(Long userId, String endpoint);
    void deleteByUserIdAndEndpoint(Long userId, String endpoint);
    void delete(PushSubscription subscription);
}
