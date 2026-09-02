package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper;

import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherUsageStatus;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherUsageJpaEntity;

public class VoucherMapper {

    public Voucher toDomain(VoucherJpaEntity entity) {
        return Voucher.reconstitute(
                entity.getId(), entity.getCode(), entity.getName(), entity.getDescription(),
                entity.getDiscountType() != null ? VoucherDiscountType.valueOf(entity.getDiscountType()) : null,
                entity.getDiscountValue(), entity.getMaxDiscountAmount(), entity.getMinOrderAmount(),
                entity.getStartAt(), entity.getEndAt(),
                entity.getUsageLimitTotal(), entity.getUsageLimitPerUser(),
                entity.getUsedCount(), entity.getStatus()
        );
    }

    public VoucherJpaEntity applyTo(VoucherJpaEntity entity, Voucher domain) {

        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setDiscountType(domain.getDiscountType() != null ? domain.getDiscountType().name() : null);
        entity.setDiscountValue(domain.getDiscountValue());
        entity.setMaxDiscountAmount(domain.getMaxDiscountAmount());
        entity.setMinOrderAmount(domain.getMinOrderAmount());
        entity.setStartAt(domain.getStartAt());
        entity.setEndAt(domain.getEndAt());
        entity.setUsageLimitTotal(domain.getUsageLimitTotal());
        entity.setUsageLimitPerUser(domain.getUsageLimitPerUser());
        entity.setUsedCount(domain.getUsedCount());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public VoucherUsage toUsageDomain(VoucherUsageJpaEntity entity) {
        return VoucherUsage.reconstitute(
                entity.getId(), entity.getVoucherId(), entity.getUserId(), entity.getOrderId(),
                entity.getDiscountAmount(),
                entity.getStatus() != null ? VoucherUsageStatus.valueOf(entity.getStatus()) : null,
                entity.getReservedAt(), entity.getCommittedAt(), entity.getExpiredAt()
        );
    }

    public VoucherUsageJpaEntity toUsageJpaEntity(VoucherUsage domain) {
        VoucherUsageJpaEntity entity = new VoucherUsageJpaEntity();
        entity.setId(domain.getId());
        entity.setVoucherId(domain.getVoucherId());
        entity.setUserId(domain.getUserId());
        entity.setOrderId(domain.getOrderId());
        entity.setDiscountAmount(domain.getDiscountAmount());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        entity.setReservedAt(domain.getReservedAt());
        entity.setCommittedAt(domain.getCommittedAt());
        entity.setExpiredAt(domain.getExpiredAt());
        return entity;
    }
}
