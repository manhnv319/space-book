package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class VoucherPersistenceAdapter implements VoucherRepository {

    private final JpaVoucherRepository jpaRepository;

    public VoucherPersistenceAdapter(JpaVoucherRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Nạp bản ghi đang được quản lý rồi cập nhật lên đó, thay vì dựng một entity
     * mới có sẵn id.
     *
     * Entity này có `@Version`. Một entity dựng tay có id nhưng version null là
     * detached với optimistic lock chưa khởi tạo, và Hibernate từ chối lưu
     * ({@code DataIntegrityViolationException}) — nghĩa là mọi lần CẬP NHẬT đều
     * hỏng, chỉ INSERT (id null) chạy được. Nạp lại trước còn giữ đúng ý nghĩa
     * của optimistic locking: version hiện tại đi cùng bản ghi.
     */
    @Override
    public Voucher save(Voucher voucher) {
        VoucherJpaEntity entity = voucher.getId() == null ? new VoucherJpaEntity()
                : jpaRepository.findById(voucher.getId()).orElseGet(VoucherJpaEntity::new);
        return toDomain(jpaRepository.save(applyTo(entity, voucher)));
    }

    @Override
    public Optional<Voucher> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Voucher> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public PageResult<Voucher> findAll(Byte status, String discountType, String search,
                                          LocalDate fromDate, LocalDate toDate, int page, int size) {
        Page<VoucherJpaEntity> result = jpaRepository.findAllWithFilters(status, discountType, search,
                toDateTime(fromDate, false), toDateTime(toDate, true),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean tryIncrementUsage(Long voucherId) {
        return jpaRepository.tryIncrementUsage(voucherId) > 0;
    }

    @Override
    public boolean decrementUsage(Long voucherId) {
        return jpaRepository.decrementUsage(voucherId) > 0;
    }

    private static LocalDateTime toDateTime(LocalDate date, boolean endOfDay) {
        if (date == null) return null;
        return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
    }

    private Voucher toDomain(VoucherJpaEntity e) {
        return Voucher.reconstitute(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                e.getDiscountType() != null ? VoucherDiscountType.valueOf(e.getDiscountType()) : null,
                e.getDiscountValue(), e.getMaxDiscountAmount(), e.getMinOrderAmount(),
                e.getStartAt(), e.getEndAt(), e.getUsageLimitTotal(), e.getUsageLimitPerUser(),
                e.getUsedCount(), e.getStatus());
    }

    private VoucherJpaEntity applyTo(VoucherJpaEntity e, Voucher d) {
        e.setId(d.getId());
        e.setCode(d.getCode());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setDiscountType(d.getDiscountType() != null ? d.getDiscountType().name() : null);
        e.setDiscountValue(d.getDiscountValue());
        e.setMaxDiscountAmount(d.getMaxDiscountAmount());
        e.setMinOrderAmount(d.getMinOrderAmount());
        e.setStartAt(d.getStartAt());
        e.setEndAt(d.getEndAt());
        e.setUsageLimitTotal(d.getUsageLimitTotal());
        e.setUsageLimitPerUser(d.getUsageLimitPerUser());
        e.setUsedCount(d.getUsedCount());
        e.setStatus(d.getStatus());
        return e;
    }
}
