package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.Subscription;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(Long id);
    List<Subscription> findByIds(Collection<Long> ids);
    List<Subscription> findAllActive();
    List<Subscription> findAll();
    void deleteById(Long id);
}
