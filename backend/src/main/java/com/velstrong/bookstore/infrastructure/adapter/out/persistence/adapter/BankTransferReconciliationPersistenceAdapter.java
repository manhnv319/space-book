package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;
import com.velstrong.bookstore.domain.port.out.BankTransferReconciliationRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.ProcessedBankMessageJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UnmatchedTransferJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaProcessedBankMessageRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUnmatchedTransferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class BankTransferReconciliationPersistenceAdapter implements BankTransferReconciliationRepository {
    private final JpaProcessedBankMessageRepository processed;
    private final JpaUnmatchedTransferRepository unmatched;

    public BankTransferReconciliationPersistenceAdapter(JpaProcessedBankMessageRepository processed,
                                                        JpaUnmatchedTransferRepository unmatched) {
        this.processed = processed;
        this.unmatched = unmatched;
    }

    @Override
    public boolean existsProcessed(String messageId, String bankTxnRef) {
        return processed.existsByMessageId(messageId)
                || (bankTxnRef != null && processed.existsByBankTxnRef(bankTxnRef));
    }

    @Override
    public void saveProcessed(String messageId, String bankTxnRef, LocalDateTime processedAt) {
        ProcessedBankMessageJpaEntity entity = new ProcessedBankMessageJpaEntity();
        entity.setMessageId(messageId);
        entity.setBankTxnRef(bankTxnRef);
        entity.setProcessedAt(processedAt);
        processed.save(entity);
    }

    @Override
    public void saveUnmatched(String messageId, String bankTxnRef, String paymentReference, long amount,
                              LocalDateTime receivedAt, String reason, LocalDateTime createdAt) {
        UnmatchedTransferJpaEntity entity = new UnmatchedTransferJpaEntity();
        entity.setMessageId(messageId);
        entity.setBankTxnRef(bankTxnRef);
        entity.setPaymentReference(paymentReference);
        entity.setAmount(amount);
        entity.setReceivedAt(receivedAt);
        entity.setReason(reason);
        entity.setCreatedAt(createdAt);
        unmatched.save(entity);
    }

    @Override
    public PageResult<UnmatchedTransfer> findUnmatched(int page, int size) {
        Page<UnmatchedTransferJpaEntity> found =
                unmatched.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return new PageResult<>(found.getContent().stream().map(this::toDomain).toList(), found.getTotalElements());
    }

    @Override public Optional<UnmatchedTransfer> findById(Long id) { return unmatched.findById(id).map(this::toDomain); }
    @Override public void deleteById(Long id) { unmatched.deleteById(id); }

    private UnmatchedTransfer toDomain(UnmatchedTransferJpaEntity entity) {
        return new UnmatchedTransfer(entity.getId(), entity.getPaymentReference(), entity.getAmount(),
                entity.getReceivedAt(), entity.getReason(), entity.getCreatedAt());
    }
}
