package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface JpaRentalRepository extends JpaRepository<RentalJpaEntity, Long> {
    boolean existsByOrderItemId(Long orderItemId);

    @Query("SELECT r FROM RentalJpaEntity r WHERE r.userId = :userId AND (:status IS NULL OR r.status = :status)")
    Page<RentalJpaEntity> findByUserIdWithStatus(Long userId, String status, Pageable pageable);

    @Query("SELECT r FROM RentalJpaEntity r WHERE (:status IS NULL OR r.status = :status)")
    Page<RentalJpaEntity> findAllWithStatus(String status, Pageable pageable);

    @Query("SELECT r FROM RentalJpaEntity r WHERE r.actualReturnDate IS NULL AND r.plannedReturnDate < :today AND r.status != 'RETURNED'")
    Page<RentalJpaEntity> findOverdue(LocalDate today, Pageable pageable);
}
