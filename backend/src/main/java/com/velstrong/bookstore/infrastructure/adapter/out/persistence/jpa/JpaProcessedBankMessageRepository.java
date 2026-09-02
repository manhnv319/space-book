package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.ProcessedBankMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedBankMessageRepository extends JpaRepository<ProcessedBankMessageJpaEntity, Long> {
    boolean existsByMessageId(String messageId);
    boolean existsByBankTxnRef(String bankTxnRef);
}
