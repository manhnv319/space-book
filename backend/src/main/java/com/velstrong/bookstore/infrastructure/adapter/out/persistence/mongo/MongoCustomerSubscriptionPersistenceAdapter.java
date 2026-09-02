package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CustomerSubscriptionJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoCustomerSubscriptionPersistenceAdapter extends MongoPersistenceSupport implements CustomerSubscriptionRepository {

    private static final String COLLECTION = "customer_subscriptions";

    public MongoCustomerSubscriptionPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public CustomerSubscription save(CustomerSubscription value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<CustomerSubscription> findById(Long id) { return findById(COLLECTION, CustomerSubscriptionJpaEntity.class, id).map(this::toDomain); }

    @Override
    public Optional<CustomerSubscription> findActiveByUserId(Long userId) {
        Criteria criteria = new Criteria().andOperator(Criteria.where("userId").is(userId),
                Criteria.where("status").is(CustomerSubscriptionStatus.ACTIVE.name()),
                Criteria.where("endDate").gte(LocalDate.now()));
        return findOne(COLLECTION, CustomerSubscriptionJpaEntity.class, Query.query(criteria)).map(this::toDomain);
    }

    @Override
    public PageResult<CustomerSubscription> findByUserId(Long userId, int page, int size) {
        Query query = Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "_id"));
        List<CustomerSubscription> values = find(COLLECTION, CustomerSubscriptionJpaEntity.class,
                query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), CustomerSubscriptionJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    private CustomerSubscription toDomain(CustomerSubscriptionJpaEntity e) {
        return CustomerSubscription.reconstitute(e.getId(), e.getUserId(), e.getSubscriptionId(), e.getStartDate(),
                e.getEndDate(), e.getUsedRentals(), e.getStatus() == null ? null : CustomerSubscriptionStatus.valueOf(e.getStatus()), null);
    }

    private CustomerSubscriptionJpaEntity toEntity(CustomerSubscription d) {
        CustomerSubscriptionJpaEntity e = new CustomerSubscriptionJpaEntity(); e.setId(d.getId()); e.setUserId(d.getUserId());
        e.setSubscriptionId(d.getSubscriptionId()); e.setStartDate(d.getStartDate()); e.setEndDate(d.getEndDate());
        e.setUsedRentals(d.getUsedRentals()); e.setStatus(d.getStatus() == null ? null : d.getStatus().name()); return e;
    }
}
