package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PushSubscription;
import com.velstrong.bookstore.domain.port.out.PushSubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoPushSubscriptionPersistenceAdapter extends MongoPersistenceSupport implements PushSubscriptionRepository {

    private static final String COLLECTION = "push_subscriptions";

    public MongoPushSubscriptionPersistenceAdapter(MongoTemplate mongo) { super(mongo); }
    @Override public PushSubscription save(PushSubscription value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public List<PushSubscription> findByUserId(Long userId) { return find(COLLECTION, PushSubscriptionJpaEntity.class, Query.query(Criteria.where("userId").is(userId))).stream().map(this::toDomain).toList(); }
    @Override public Optional<PushSubscription> findByUserIdAndEndpoint(Long userId, String endpoint) { return findOne(COLLECTION, PushSubscriptionJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("endpoint").is(endpoint)))).map(this::toDomain); }
    @Override public void deleteByUserIdAndEndpoint(Long userId, String endpoint) { mongo.remove(Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("endpoint").is(endpoint))), COLLECTION); }
    @Override public void delete(PushSubscription value) { if (value.id() != null) deleteById(COLLECTION, value.id(), PushSubscriptionJpaEntity.class); }
    private PushSubscription toDomain(PushSubscriptionJpaEntity e) { return new PushSubscription(e.getId(), e.getUserId(), e.getEndpoint(), e.getP256dh(), e.getAuth(), e.getCreatedAt()); }
    private PushSubscriptionJpaEntity toEntity(PushSubscription d) { PushSubscriptionJpaEntity e = new PushSubscriptionJpaEntity(); e.setId(d.id()); e.setUserId(d.userId()); e.setEndpoint(d.endpoint()); e.setP256dh(d.p256dh()); e.setAuth(d.auth()); e.setCreatedAt(d.createdAt()); return e; }
}
