package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderStatusHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryJpaEntity, Long> {
    List<OrderStatusHistoryJpaEntity> findByOrderIdOrderByChangedAtAsc(Long orderId);
}
