package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Subscription;
import com.velstrong.bookstore.domain.model.enums.subscription.SubscriptionStatus;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaSubscriptionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class SubscriptionPersistenceAdapter implements SubscriptionRepository {

    private final JpaSubscriptionRepository jpaRepository;

    public SubscriptionPersistenceAdapter(JpaSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        return toDomain(jpaRepository.save(toJpaEntity(subscription)));
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Subscription> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return jpaRepository.findAllById(ids).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Subscription> findAllActive() {
        return jpaRepository.findByStatus(SubscriptionStatus.ACTIVE.name())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Subscription> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Subscription toDomain(SubscriptionJpaEntity e) {
        return Subscription.reconstitute(e.getId(), e.getName(), e.getDescription(), e.getPrice(),
                e.getDurationDays(), e.getMaxRentals(),
                e.getStatus() != null ? SubscriptionStatus.valueOf(e.getStatus()) : null);
    }

    private SubscriptionJpaEntity toJpaEntity(Subscription d) {
        SubscriptionJpaEntity e = new SubscriptionJpaEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setPrice(d.getPrice());
        e.setDurationDays(d.getDurationDays());
        e.setMaxRentals(d.getMaxRentals());
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        return e;
    }
}
