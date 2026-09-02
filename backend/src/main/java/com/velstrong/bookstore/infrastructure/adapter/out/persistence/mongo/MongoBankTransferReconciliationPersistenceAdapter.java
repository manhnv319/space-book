package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;
import com.velstrong.bookstore.domain.port.out.BankTransferReconciliationRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.ProcessedBankMessageJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UnmatchedTransferJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoBankTransferReconciliationPersistenceAdapter extends MongoPersistenceSupport implements BankTransferReconciliationRepository {

    private static final String PROCESSED = "processed_bank_messages";
    private static final String UNMATCHED = "unmatched_transfers";

    public MongoBankTransferReconciliationPersistenceAdapter(MongoTemplate mongo) { super(mongo); }
    @Override public boolean existsProcessed(String messageId, String bankTxnRef) { Criteria criteria = Criteria.where("messageId").is(messageId); if (bankTxnRef != null) criteria = new Criteria().orOperator(criteria, Criteria.where("bankTxnRef").is(bankTxnRef)); return exists(PROCESSED, Query.query(criteria), ProcessedBankMessageJpaEntity.class); }

    @Override public void saveProcessed(String messageId, String bankTxnRef, LocalDateTime processedAt) { ProcessedBankMessageJpaEntity e = new ProcessedBankMessageJpaEntity(); e.setMessageId(messageId); e.setBankTxnRef(bankTxnRef); e.setProcessedAt(processedAt); save(PROCESSED, e); }

    @Override public void saveUnmatched(String messageId, String bankTxnRef, String paymentReference, long amount, LocalDateTime receivedAt, String reason, LocalDateTime createdAt) { UnmatchedTransferJpaEntity e = new UnmatchedTransferJpaEntity(); e.setMessageId(messageId); e.setBankTxnRef(bankTxnRef); e.setPaymentReference(paymentReference); e.setAmount(amount); e.setReceivedAt(receivedAt); e.setReason(reason); e.setCreatedAt(createdAt); save(UNMATCHED, e); }

    @Override public PageResult<UnmatchedTransfer> findUnmatched(int page, int size) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<UnmatchedTransfer> values = find(UNMATCHED, UnmatchedTransferJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), UnmatchedTransferJpaEntity.class, UNMATCHED);
        return PageResult.of(values, total);
    }

    @Override public Optional<UnmatchedTransfer> findById(Long id) { return findById(UNMATCHED, UnmatchedTransferJpaEntity.class, id).map(this::toDomain); }
    @Override public void deleteById(Long id) { deleteById(UNMATCHED, id, UnmatchedTransferJpaEntity.class); }
    private UnmatchedTransfer toDomain(UnmatchedTransferJpaEntity e) { return new UnmatchedTransfer(e.getId(), e.getPaymentReference(), e.getAmount(), e.getReceivedAt(), e.getReason(), e.getCreatedAt()); }
}
