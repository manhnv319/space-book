package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherUsageStatus;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherUsageJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaVoucherUsageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class VoucherUsagePersistenceAdapter implements VoucherUsageRepository {

    private final JpaVoucherUsageRepository jpaRepository;

    public VoucherUsagePersistenceAdapter(JpaVoucherUsageRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public VoucherUsage save(VoucherUsage voucherUsage) {
        return toDomain(jpaRepository.save(toJpaEntity(voucherUsage)));
    }

    @Override
    public Optional<VoucherUsage> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<VoucherUsage> findReservedByOrderId(Long orderId) {
        return jpaRepository.findReservedByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public int countCommittedByVoucherId(Long voucherId) {
        return jpaRepository.countCommittedByVoucherId(voucherId);
    }

    @Override
    public int countCommittedByVoucherIdAndUserId(Long voucherId, Long userId) {
        return jpaRepository.countCommittedByVoucherIdAndUserId(voucherId, userId);
    }

    @Override
    public List<VoucherUsage> findExpiredReservations(LocalDateTime before) {
        return jpaRepository.findByStatusAndReservedAtBefore(VoucherUsageStatus.RESERVED.name(), before)
                .stream().map(this::toDomain).toList();
    }

    private VoucherUsage toDomain(VoucherUsageJpaEntity e) {
        return VoucherUsage.reconstitute(e.getId(), e.getVoucherId(), e.getUserId(), e.getOrderId(),
                e.getDiscountAmount(),
                e.getStatus() != null ? VoucherUsageStatus.valueOf(e.getStatus()) : null,
                e.getReservedAt(), e.getCommittedAt(), e.getExpiredAt());
    }

    private VoucherUsageJpaEntity toJpaEntity(VoucherUsage d) {
        VoucherUsageJpaEntity e = new VoucherUsageJpaEntity();
        e.setId(d.getId());
        e.setVoucherId(d.getVoucherId());
        e.setUserId(d.getUserId());
        e.setOrderId(d.getOrderId());
        e.setDiscountAmount(d.getDiscountAmount());
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        e.setReservedAt(d.getReservedAt());
        e.setCommittedAt(d.getCommittedAt());
        e.setExpiredAt(d.getExpiredAt());
        return e;
    }
}
