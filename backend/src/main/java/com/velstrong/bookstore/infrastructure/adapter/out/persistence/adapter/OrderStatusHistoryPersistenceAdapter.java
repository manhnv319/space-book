package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderStatusHistoryJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaOrderStatusHistoryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("postgres & !mongodb")
public class OrderStatusHistoryPersistenceAdapter implements OrderStatusHistoryRepository {

    private final JpaOrderStatusHistoryRepository jpaRepository;

    public OrderStatusHistoryPersistenceAdapter(JpaOrderStatusHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void record(Long orderId, OrderStatus status, String source, LocalDateTime changedAt) {
        OrderStatusHistoryJpaEntity entity = new OrderStatusHistoryJpaEntity();
        entity.setOrderId(orderId);
        entity.setStatus(status.name());
        entity.setSource(source);
        entity.setChangedAt(changedAt);
        jpaRepository.save(entity);
    }

    @Override
    public List<OrderStatusChange> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderIdOrderByChangedAtAsc(orderId).stream()
                .map(e -> new OrderStatusChange(e.getId(), e.getOrderId(),
                        OrderStatus.valueOf(e.getStatus()), e.getSource(), e.getChangedAt()))
                .toList();
    }
}
