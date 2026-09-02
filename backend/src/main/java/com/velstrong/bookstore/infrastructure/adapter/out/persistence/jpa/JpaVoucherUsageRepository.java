package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherUsageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaVoucherUsageRepository extends JpaRepository<VoucherUsageJpaEntity, Long> {
    @Query("SELECT vu FROM VoucherUsageJpaEntity vu WHERE vu.orderId = :orderId AND vu.status = 'RESERVED'")
    Optional<VoucherUsageJpaEntity> findReservedByOrderId(Long orderId);

    @Query("SELECT COUNT(vu) FROM VoucherUsageJpaEntity vu WHERE vu.voucherId = :voucherId AND vu.status = 'COMMITTED'")
    int countCommittedByVoucherId(Long voucherId);

    @Query("SELECT COUNT(vu) FROM VoucherUsageJpaEntity vu WHERE vu.voucherId = :voucherId AND vu.userId = :userId AND vu.status = 'COMMITTED'")
    int countCommittedByVoucherIdAndUserId(Long voucherId, Long userId);

    List<VoucherUsageJpaEntity> findByStatusAndReservedAtBefore(String status, LocalDateTime before);
}
