package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findFirstByOrderIdOrderByCreatedAtDesc(Long orderId);
    Optional<PaymentJpaEntity> findByTransferReference(String transferReference);
    @Query("SELECT p FROM PaymentJpaEntity p WHERE p.method = 'BANK_TRANSFER' AND p.status = 'PENDING' AND p.expiresAt <= CURRENT_TIMESTAMP")
    List<PaymentJpaEntity> findExpiredPendingBankTransfers();
    List<PaymentJpaEntity> findAllByOrderId(Long orderId);
    Page<PaymentJpaEntity> findByOrderId(Long orderId, Pageable pageable);

    @Query("""
            SELECT p FROM PaymentJpaEntity p
            JOIN OrderJpaEntity o ON o.id = p.orderId
            WHERE o.userId = :userId
            ORDER BY p.createdAt DESC
            """)
    Page<PaymentJpaEntity> findByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT COUNT(p) FROM PaymentJpaEntity p
            JOIN OrderJpaEntity o ON o.id = p.orderId
            WHERE o.userId = :userId
            """)
    long countByUserId(Long userId);

    java.util.Optional<PaymentJpaEntity> findFirstByCustomerSubscriptionIdOrderByCreatedAtDesc(Long customerSubscriptionId);
}
