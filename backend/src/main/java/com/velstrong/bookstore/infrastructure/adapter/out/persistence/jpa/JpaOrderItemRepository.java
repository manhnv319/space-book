package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaOrderItemRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    List<OrderItemJpaEntity> findByOrderId(Long orderId);

    // Only orders that are not cancelled/refunded and not still pending (unpaid) count
    // towards the sales signal used for bestseller suggestions.
    @Query("""
            SELECT oi.bookId, SUM(oi.quantity)
            FROM OrderItemJpaEntity oi
            JOIN OrderJpaEntity o ON o.id = oi.orderId
            WHERE o.status IN ('CONFIRMED', 'PROCESSING', 'SHIPPING', 'COMPLETED')
              AND o.createdAt >= :since
              AND (:itemType IS NULL OR oi.itemType = :itemType)
            GROUP BY oi.bookId
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Object[]> findTopSellingBookIds(LocalDateTime since, String itemType, Pageable pageable);

    @Query("""
            SELECT oi FROM OrderItemJpaEntity oi JOIN OrderJpaEntity o ON o.id = oi.orderId
            WHERE oi.id = :orderItemId AND oi.bookId = :bookId AND o.userId = :userId AND o.paymentStatus = 'PAID'
            """)
    Optional<OrderItemJpaEntity> findPaidForReview(Long userId, Long bookId, Long orderItemId);

    @Query("""
            SELECT oi FROM OrderItemJpaEntity oi JOIN OrderJpaEntity o ON o.id = oi.orderId
            WHERE oi.bookId = :bookId AND o.userId = :userId AND o.paymentStatus = 'PAID'
            ORDER BY oi.id DESC
            """)
    List<OrderItemJpaEntity> findPaidItemsForReview(Long userId, Long bookId);
}
