package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PushSubscription;
import com.velstrong.bookstore.domain.port.out.PushSubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaPushSubscriptionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class PushSubscriptionPersistenceAdapter implements PushSubscriptionRepository {
    private final JpaPushSubscriptionRepository subscriptions;
    public PushSubscriptionPersistenceAdapter(JpaPushSubscriptionRepository subscriptions) { this.subscriptions = subscriptions; }
    @Override public PushSubscription save(PushSubscription value) { return toDomain(subscriptions.save(toEntity(value))); }
    @Override public List<PushSubscription> findByUserId(Long userId) { return subscriptions.findByUserId(userId).stream().map(this::toDomain).toList(); }
    @Override public Optional<PushSubscription> findByUserIdAndEndpoint(Long userId, String endpoint) { return subscriptions.findByUserIdAndEndpoint(userId, endpoint).map(this::toDomain); }
    @Override public void deleteByUserIdAndEndpoint(Long userId, String endpoint) { subscriptions.deleteByUserIdAndEndpoint(userId, endpoint); }
    @Override public void delete(PushSubscription value) { if (value.id() != null) subscriptions.deleteById(value.id()); }
    private PushSubscription toDomain(PushSubscriptionJpaEntity value) { return new PushSubscription(value.getId(), value.getUserId(), value.getEndpoint(), value.getP256dh(), value.getAuth(), value.getCreatedAt()); }
    private PushSubscriptionJpaEntity toEntity(PushSubscription value) { PushSubscriptionJpaEntity entity = new PushSubscriptionJpaEntity(); entity.setId(value.id()); entity.setUserId(value.userId()); entity.setEndpoint(value.endpoint()); entity.setP256dh(value.p256dh()); entity.setAuth(value.auth()); entity.setCreatedAt(value.createdAt()); return entity; }
}
