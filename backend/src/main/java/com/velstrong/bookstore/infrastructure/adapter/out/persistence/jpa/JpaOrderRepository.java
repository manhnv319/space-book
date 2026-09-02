package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaOrderRepository extends JpaRepository<OrderJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.id = :id")
    Optional<OrderJpaEntity> findByIdForUpdate(Long id);

    Optional<OrderJpaEntity> findByOrderCode(String orderCode);
    boolean existsByOrderCode(String orderCode);

    @Query("""
            SELECT o FROM OrderJpaEntity o
            WHERE o.userId = :userId
              AND (:status IS NULL OR o.status = :status)
              AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
            """)
    Page<OrderJpaEntity> findByUserIdWithFilters(Long userId, String status, String paymentStatus, Pageable pageable);

    @Query("""
            SELECT o FROM OrderJpaEntity o
            WHERE (:status IS NULL OR o.status = :status)
              AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
              AND (:search IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
              AND (:toDate IS NULL OR o.createdAt <= :toDate)
            """)
    Page<OrderJpaEntity> findAllWithFilters(String status, String paymentStatus, String search,
                                            LocalDateTime fromDate, LocalDateTime toDate,
                                            Pageable pageable);

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.status IN :statuses "
            + "AND o.paymentStatus = 'PAID' AND o.modifiedAt < :cutoff")
    List<OrderJpaEntity> findAdvanceable(@Param("statuses") List<String> statuses,
                                         @Param("cutoff") LocalDateTime cutoff);

    Page<OrderJpaEntity> findByUserIdAndStatusInOrderByCreatedAtDesc(Long userId, List<String> statuses,
                                                                     Pageable pageable);

    Page<OrderJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT o.status, COUNT(o) FROM OrderJpaEntity o WHERE o.userId = :userId GROUP BY o.status")
    List<Object[]> countByStatus(@Param("userId") Long userId);
}
