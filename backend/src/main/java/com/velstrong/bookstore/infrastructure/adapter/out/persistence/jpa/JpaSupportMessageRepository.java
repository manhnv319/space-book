package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSupportMessageRepository extends JpaRepository<SupportMessageJpaEntity, Long> {
    List<SupportMessageJpaEntity> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
