package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalFulfillmentJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaRentalFulfillmentRepository extends JpaRepository<RentalFulfillmentJpaEntity, Long> {
    Optional<RentalFulfillmentJpaEntity> findByOrderId(Long orderId);
    List<RentalFulfillmentJpaEntity> findByStatusInOrderByUpdatedAtAsc(Collection<String> statuses, Pageable pageable);
}
