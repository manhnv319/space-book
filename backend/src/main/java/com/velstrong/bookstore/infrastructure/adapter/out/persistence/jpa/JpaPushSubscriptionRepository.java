package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPushSubscriptionRepository extends JpaRepository<PushSubscriptionJpaEntity, Long> {
    List<PushSubscriptionJpaEntity> findByUserId(Long userId);
    Optional<PushSubscriptionJpaEntity> findByUserIdAndEndpoint(Long userId, String endpoint);
    void deleteByUserIdAndEndpoint(Long userId, String endpoint);
}
