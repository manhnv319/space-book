package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportConversationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSupportConversationRepository extends JpaRepository<SupportConversationJpaEntity, Long> {
    Optional<SupportConversationJpaEntity> findByUserId(Long userId);
    Page<SupportConversationJpaEntity> findAllByOrderByLastMessageAtDesc(Pageable pageable);
    long countByStaffUnreadCountGreaterThan(int count);
}
