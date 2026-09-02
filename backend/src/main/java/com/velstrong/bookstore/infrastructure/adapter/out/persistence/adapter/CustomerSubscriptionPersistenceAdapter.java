package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CustomerSubscriptionJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCustomerSubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class CustomerSubscriptionPersistenceAdapter implements CustomerSubscriptionRepository {

    private final JpaCustomerSubscriptionRepository jpaRepository;

    public CustomerSubscriptionPersistenceAdapter(JpaCustomerSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustomerSubscription save(CustomerSubscription cs) {
        return toDomain(jpaRepository.save(toJpaEntity(cs)));
    }

    @Override
    public Optional<CustomerSubscription> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<CustomerSubscription> findActiveByUserId(Long userId) {
        return jpaRepository.findActiveByUserId(userId).map(this::toDomain);
    }

    @Override
    public PageResult<CustomerSubscription> findByUserId(Long userId, int page, int size) {
        Page<CustomerSubscriptionJpaEntity> result = jpaRepository.findByUserId(userId, PageRequest.of(page, size));
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }

    private CustomerSubscription toDomain(CustomerSubscriptionJpaEntity e) {
        return CustomerSubscription.reconstitute(e.getId(), e.getUserId(), e.getSubscriptionId(),
                e.getStartDate(), e.getEndDate(), e.getUsedRentals(),
                e.getStatus() != null ? CustomerSubscriptionStatus.valueOf(e.getStatus()) : null, null);
    }

    private CustomerSubscriptionJpaEntity toJpaEntity(CustomerSubscription d) {
        CustomerSubscriptionJpaEntity e = new CustomerSubscriptionJpaEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setSubscriptionId(d.getSubscriptionId());
        e.setStartDate(d.getStartDate());
        e.setEndDate(d.getEndDate());
        e.setUsedRentals(d.getUsedRentals());
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        return e;
    }
}
