package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentTransactionStatus;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoPaymentPersistenceAdapter extends MongoPersistenceSupport implements PaymentRepository {

    private static final String COLLECTION = "payments";
    private static final String ORDERS = "orders";

    public MongoPaymentPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Payment save(Payment value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<Payment> findById(Long id) { return findById(COLLECTION, PaymentJpaEntity.class, id).map(this::toDomain); }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return findOne(COLLECTION, PaymentJpaEntity.class, Query.query(Criteria.where("orderId").is(orderId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByCustomerSubscriptionId(Long customerSubscriptionId) {
        return findOne(COLLECTION, PaymentJpaEntity.class, Query.query(Criteria.where("customerSubscriptionId").is(customerSubscriptionId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))).map(this::toDomain);
    }

    @Override public Optional<Payment> findByTransferReference(String transferReference) {
        return findOne(COLLECTION, PaymentJpaEntity.class, Query.query(Criteria.where("transferReference").is(transferReference))).map(this::toDomain);
    }

    @Override
    public List<Payment> findExpiredPendingBankTransfers() {
        Criteria criteria = new Criteria().andOperator(Criteria.where("method").is(PaymentMethod.BANK_TRANSFER.name()),
                Criteria.where("status").is(PaymentTransactionStatus.PENDING.name()), Criteria.where("expiresAt").lte(LocalDateTime.now()));
        return find(COLLECTION, PaymentJpaEntity.class, Query.query(criteria)).stream().map(this::toDomain).toList();
    }

    @Override public List<Payment> findAllByOrderId(Long orderId) { return find(COLLECTION, PaymentJpaEntity.class, Query.query(Criteria.where("orderId").is(orderId))).stream().map(this::toDomain).toList(); }

    @Override
    public PageResult<Payment> findByUserId(Long userId, int page, int size) {
        List<Long> orderIds = mongo.find(Query.query(Criteria.where("userId").is(userId)), Document.class, ORDERS).stream()
                .map(value -> ((Number) value.get("_id")).longValue()).toList();
        if (orderIds.isEmpty()) return PageResult.of(List.of(), 0);
        Query query = Query.query(Criteria.where("orderId").in(orderIds)).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Payment> values = find(COLLECTION, PaymentJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), PaymentJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    private Payment toDomain(PaymentJpaEntity e) {
        return Payment.reconstitute(e.getId(), e.getOrderId(), e.getCustomerSubscriptionId(), e.getAmount(),
                e.getMethod() == null ? null : PaymentMethod.valueOf(e.getMethod()),
                e.getStatus() == null ? null : PaymentTransactionStatus.valueOf(e.getStatus()), e.getTransactionId(),
                e.getGatewayRef(), e.getTransferReference(), e.getExpiresAt(), e.getPaidAt(), e.getCreatedAt());
    }

    private PaymentJpaEntity toEntity(Payment d) {
        PaymentJpaEntity e = new PaymentJpaEntity(); e.setId(d.getId()); e.setOrderId(d.getOrderId());
        e.setCustomerSubscriptionId(d.getCustomerSubscriptionId()); e.setAmount(d.getAmount());
        e.setMethod(d.getMethod() == null ? null : d.getMethod().name()); e.setStatus(d.getStatus() == null ? null : d.getStatus().name());
        e.setTransactionId(d.getTransactionId()); e.setGatewayRef(d.getGatewayRef()); e.setTransferReference(d.getTransferReference());
        e.setExpiresAt(d.getExpiresAt()); e.setPaidAt(d.getPaidAt()); e.setCreatedAt(d.getCreatedAt()); return e;
    }
}
