package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.BookSalesCount;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaOrderItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.OrderItemMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * F21: standalone adapter for {@link OrderItemRepository}, split out from
 * {@code OrderPersistenceAdapter} so each port has one focused adapter.
 */
@Component
@Profile("postgres & !mongodb")
public class OrderItemPersistenceAdapter implements OrderItemRepository {

    private final JpaOrderItemRepository jpaRepository;
    private final OrderItemMapper mapper;

    public OrderItemPersistenceAdapter(JpaOrderItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = new OrderItemMapper();
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(orderItem)));
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> items) {
        List<com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity> entities =
                items.stream().map(mapper::toJpaEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<BookSalesCount> findTopSellingBooks(LocalDateTime since, String itemType, int limit) {
        List<Object[]> rows = jpaRepository.findTopSellingBookIds(since, itemType, PageRequest.of(0, limit));
        return rows.stream()
                .map(row -> new BookSalesCount(((Number) row[0]).longValue(), ((Number) row[1]).longValue()))
                .toList();
    }
}
