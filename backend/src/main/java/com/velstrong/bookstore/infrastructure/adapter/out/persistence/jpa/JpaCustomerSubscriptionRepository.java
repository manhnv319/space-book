package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CustomerSubscriptionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaCustomerSubscriptionRepository extends JpaRepository<CustomerSubscriptionJpaEntity, Long> {
    @Query("SELECT cs FROM CustomerSubscriptionJpaEntity cs WHERE cs.userId = :userId AND cs.status = 'ACTIVE' AND cs.endDate >= CURRENT_DATE")
    Optional<CustomerSubscriptionJpaEntity> findActiveByUserId(Long userId);

    Page<CustomerSubscriptionJpaEntity> findByUserId(Long userId, Pageable pageable);
}
