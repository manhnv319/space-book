package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportMessageAttachmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSupportMessageAttachmentRepository extends JpaRepository<SupportMessageAttachmentJpaEntity, Long> {
    List<SupportMessageAttachmentJpaEntity> findByMessageIdInOrderByIdAsc(List<Long> messageIds);
}
