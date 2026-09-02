package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
    List<SubscriptionJpaEntity> findByStatus(String status);
}
