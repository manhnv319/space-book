package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.model.enums.subscription.SubscriptionStatus;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoSubscriptionPersistenceAdapter extends MongoPersistenceSupport implements SubscriptionRepository {

    private static final String COLLECTION = "subscriptions";

    public MongoSubscriptionPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Subscription save(Subscription value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<Subscription> findById(Long id) { return findById(COLLECTION, SubscriptionJpaEntity.class, id).map(this::toDomain); }
    @Override public List<Subscription> findByIds(Collection<Long> ids) { if (ids == null || ids.isEmpty()) return List.of(); return find(COLLECTION, SubscriptionJpaEntity.class, Query.query(Criteria.where("_id").in(ids))).stream().map(this::toDomain).toList(); }
    @Override public List<Subscription> findAllActive() { return find(COLLECTION, SubscriptionJpaEntity.class, Query.query(Criteria.where("status").is(SubscriptionStatus.ACTIVE.name()))).stream().map(this::toDomain).toList(); }
    @Override public List<Subscription> findAll() { return find(COLLECTION, SubscriptionJpaEntity.class, new Query()).stream().map(this::toDomain).toList(); }
    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, SubscriptionJpaEntity.class); }

    private Subscription toDomain(SubscriptionJpaEntity e) {
        return Subscription.reconstitute(e.getId(), e.getName(), e.getDescription(), e.getPrice(), e.getDurationDays(),
                e.getMaxRentals(), e.getStatus() == null ? null : SubscriptionStatus.valueOf(e.getStatus()));
    }

    private SubscriptionJpaEntity toEntity(Subscription d) {
        SubscriptionJpaEntity e = new SubscriptionJpaEntity(); e.setId(d.getId()); e.setName(d.getName()); e.setDescription(d.getDescription());
        e.setPrice(d.getPrice()); e.setDurationDays(d.getDurationDays()); e.setMaxRentals(d.getMaxRentals());
        e.setStatus(d.getStatus() == null ? null : d.getStatus().name()); return e;
    }
}
