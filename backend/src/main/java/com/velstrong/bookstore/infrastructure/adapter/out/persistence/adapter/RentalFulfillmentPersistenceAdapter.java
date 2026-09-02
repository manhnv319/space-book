package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.RentalFulfillment;
import com.velstrong.bookstore.domain.model.enums.rental.RentalFulfillmentStatus;
import com.velstrong.bookstore.domain.port.out.RentalFulfillmentRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalFulfillmentJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaRentalFulfillmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class RentalFulfillmentPersistenceAdapter implements RentalFulfillmentRepository {

    private final JpaRentalFulfillmentRepository jpaRepository;

    public RentalFulfillmentPersistenceAdapter(JpaRentalFulfillmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RentalFulfillment save(RentalFulfillment fulfillment) {
        return toDomain(jpaRepository.save(toEntity(fulfillment)));
    }

    @Override
    public Optional<RentalFulfillment> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public List<RentalFulfillment> findRetryable(int limit) {
        return jpaRepository.findByStatusInOrderByUpdatedAtAsc(
                List.of(RentalFulfillmentStatus.PENDING.name(), RentalFulfillmentStatus.FAILED.name()),
                PageRequest.of(0, limit)).stream().map(this::toDomain).toList();
    }

    private RentalFulfillment toDomain(RentalFulfillmentJpaEntity entity) {
        return RentalFulfillment.reconstitute(entity.getId(), entity.getOrderId(),
                RentalFulfillmentStatus.valueOf(entity.getStatus()), entity.getAttempts(), entity.getErrorMessage(),
                entity.getCreatedAt(), entity.getUpdatedAt(), entity.getCompletedAt());
    }

    private RentalFulfillmentJpaEntity toEntity(RentalFulfillment domain) {
        RentalFulfillmentJpaEntity entity = new RentalFulfillmentJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setStatus(domain.getStatus().name());
        entity.setAttempts(domain.getAttempts());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        return entity;
    }
}
