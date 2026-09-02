package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.RentalFulfillment;
import com.velstrong.bookstore.domain.model.enums.rental.RentalFulfillmentStatus;
import com.velstrong.bookstore.domain.port.out.RentalFulfillmentRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalFulfillmentJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoRentalFulfillmentPersistenceAdapter extends MongoPersistenceSupport implements RentalFulfillmentRepository {

    private static final String COLLECTION = "rental_fulfillments";

    public MongoRentalFulfillmentPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public RentalFulfillment save(RentalFulfillment value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<RentalFulfillment> findByOrderId(Long orderId) { return findOne(COLLECTION, RentalFulfillmentJpaEntity.class, Query.query(Criteria.where("orderId").is(orderId))).map(this::toDomain); }

    @Override
    public List<RentalFulfillment> findRetryable(int limit) {
        Query query = Query.query(Criteria.where("status").in(List.of(RentalFulfillmentStatus.PENDING.name(), RentalFulfillmentStatus.FAILED.name())))
                .with(Sort.by(Sort.Direction.ASC, "updatedAt")).limit(limit);
        return find(COLLECTION, RentalFulfillmentJpaEntity.class, query).stream().map(this::toDomain).toList();
    }

    private RentalFulfillment toDomain(RentalFulfillmentJpaEntity e) {
        return RentalFulfillment.reconstitute(e.getId(), e.getOrderId(), RentalFulfillmentStatus.valueOf(e.getStatus()),
                e.getAttempts(), e.getErrorMessage(), e.getCreatedAt(), e.getUpdatedAt(), e.getCompletedAt());
    }

    private RentalFulfillmentJpaEntity toEntity(RentalFulfillment d) {
        RentalFulfillmentJpaEntity e = new RentalFulfillmentJpaEntity(); e.setId(d.getId()); e.setOrderId(d.getOrderId());
        e.setStatus(d.getStatus().name()); e.setAttempts(d.getAttempts()); e.setErrorMessage(d.getErrorMessage());
        e.setCreatedAt(d.getCreatedAt()); e.setUpdatedAt(d.getUpdatedAt()); e.setCompletedAt(d.getCompletedAt()); return e;
    }
}
