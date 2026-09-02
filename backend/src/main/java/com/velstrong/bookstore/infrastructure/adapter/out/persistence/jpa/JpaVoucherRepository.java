package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaVoucherRepository extends JpaRepository<VoucherJpaEntity, Long> {
    Optional<VoucherJpaEntity> findByCode(String code);

    @Query("""
            SELECT v FROM VoucherJpaEntity v
            WHERE (:status IS NULL OR v.status = :status)
              AND (:discountType IS NULL OR v.discountType = :discountType)
              AND (:search IS NULL OR LOWER(v.code) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(v.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:fromDate IS NULL OR v.startAt >= :fromDate)
              AND (:toDate IS NULL OR v.endAt <= :toDate)
            """)
    Page<VoucherJpaEntity> findAllWithFilters(Byte status, String discountType, String search,
                                              LocalDateTime fromDate, LocalDateTime toDate,
                                              Pageable pageable);

    @Modifying
    @Query("UPDATE VoucherJpaEntity v SET v.usedCount = v.usedCount + 1 " +
           "WHERE v.id = :id AND (v.usageLimitTotal IS NULL OR v.usedCount < v.usageLimitTotal)")
    int tryIncrementUsage(Long id);

    @Modifying
    @Query("UPDATE VoucherJpaEntity v SET v.usedCount = v.usedCount - 1 " +
           "WHERE v.id = :id AND v.usedCount > 0")
    int decrementUsage(Long id);
}
