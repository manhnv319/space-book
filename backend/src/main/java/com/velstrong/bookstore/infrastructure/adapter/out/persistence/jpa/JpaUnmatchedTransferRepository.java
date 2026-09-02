package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UnmatchedTransferJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUnmatchedTransferRepository extends JpaRepository<UnmatchedTransferJpaEntity, Long> {
    Page<UnmatchedTransferJpaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
