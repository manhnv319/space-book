package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherUsageStatus;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherUsageJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoVoucherUsagePersistenceAdapter extends MongoPersistenceSupport implements VoucherUsageRepository {

    private static final String COLLECTION = "voucher_usages";

    public MongoVoucherUsagePersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public VoucherUsage save(VoucherUsage value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<VoucherUsage> findById(Long id) { return findById(COLLECTION, VoucherUsageJpaEntity.class, id).map(this::toDomain); }
    @Override public Optional<VoucherUsage> findReservedByOrderId(Long orderId) { return findOne(COLLECTION, VoucherUsageJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("orderId").is(orderId), Criteria.where("status").is(VoucherUsageStatus.RESERVED.name())))).map(this::toDomain); }
    @Override public int countCommittedByVoucherId(Long voucherId) { return (int) count(COLLECTION, Query.query(new Criteria().andOperator(Criteria.where("voucherId").is(voucherId), Criteria.where("status").is(VoucherUsageStatus.COMMITTED.name()))), VoucherUsageJpaEntity.class); }
    @Override public int countCommittedByVoucherIdAndUserId(Long voucherId, Long userId) { return (int) count(COLLECTION, Query.query(new Criteria().andOperator(Criteria.where("voucherId").is(voucherId), Criteria.where("userId").is(userId), Criteria.where("status").is(VoucherUsageStatus.COMMITTED.name()))), VoucherUsageJpaEntity.class); }
    @Override public List<VoucherUsage> findExpiredReservations(LocalDateTime before) { return find(COLLECTION, VoucherUsageJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("status").is(VoucherUsageStatus.RESERVED.name()), Criteria.where("reservedAt").lt(before)))).stream().map(this::toDomain).toList(); }

    private VoucherUsage toDomain(VoucherUsageJpaEntity e) {
        return VoucherUsage.reconstitute(e.getId(), e.getVoucherId(), e.getUserId(), e.getOrderId(), e.getDiscountAmount(),
                e.getStatus() == null ? null : VoucherUsageStatus.valueOf(e.getStatus()), e.getReservedAt(), e.getCommittedAt(), e.getExpiredAt());
    }

    private VoucherUsageJpaEntity toEntity(VoucherUsage d) {
        VoucherUsageJpaEntity e = new VoucherUsageJpaEntity(); e.setId(d.getId()); e.setVoucherId(d.getVoucherId()); e.setUserId(d.getUserId()); e.setOrderId(d.getOrderId());
        e.setDiscountAmount(d.getDiscountAmount()); e.setStatus(d.getStatus() == null ? null : d.getStatus().name()); e.setReservedAt(d.getReservedAt()); e.setCommittedAt(d.getCommittedAt()); e.setExpiredAt(d.getExpiredAt()); return e;
    }
}
