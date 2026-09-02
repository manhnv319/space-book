package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserNotificationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserNotificationRepository extends JpaRepository<UserNotificationJpaEntity, Long> {
    Optional<UserNotificationJpaEntity> findByIdAndUserId(Long id, Long userId);
    Page<UserNotificationJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByUserIdAndReadAtIsNull(Long userId);
    long deleteByUserIdAndReadAtIsNotNull(Long userId);
}
