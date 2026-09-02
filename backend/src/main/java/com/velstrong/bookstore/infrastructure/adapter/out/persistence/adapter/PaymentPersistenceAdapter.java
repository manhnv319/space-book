package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentTransactionStatus;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaPaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class PaymentPersistenceAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;

    public PaymentPersistenceAdapter(JpaPaymentRepository jpaRepository) {
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
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = payment.getId() == null ? new PaymentJpaEntity()
                : jpaRepository.findById(payment.getId()).orElseGet(PaymentJpaEntity::new);
        return toDomain(jpaRepository.save(applyTo(entity, payment)));
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return jpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByCustomerSubscriptionId(Long customerSubscriptionId) {
        return jpaRepository.findFirstByCustomerSubscriptionIdOrderByCreatedAtDesc(customerSubscriptionId)
                .map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByTransferReference(String transferReference) {
        return jpaRepository.findByTransferReference(transferReference).map(this::toDomain);
    }

    @Override
    public List<Payment> findExpiredPendingBankTransfers() {
        return jpaRepository.findExpiredPendingBankTransfers().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Payment> findAllByOrderId(Long orderId) {
        return jpaRepository.findAllByOrderId(orderId).stream().map(this::toDomain).toList();
    }

    @Override
    public PageResult<Payment> findByUserId(Long userId, int page, int size) {
        Page<PaymentJpaEntity> result = jpaRepository.findByUserId(userId, PageRequest.of(page, size));
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }

    private Payment toDomain(PaymentJpaEntity e) {
        return Payment.reconstitute(e.getId(), e.getOrderId(), e.getCustomerSubscriptionId(), e.getAmount(),
                e.getMethod() != null ? PaymentMethod.valueOf(e.getMethod()) : null,
                e.getStatus() != null ? PaymentTransactionStatus.valueOf(e.getStatus()) : null,
                e.getTransactionId(), e.getGatewayRef(), e.getTransferReference(), e.getExpiresAt(),
                e.getPaidAt(), e.getCreatedAt());
    }

    private PaymentJpaEntity applyTo(PaymentJpaEntity e, Payment d) {
        e.setId(d.getId());
        e.setOrderId(d.getOrderId());
        e.setCustomerSubscriptionId(d.getCustomerSubscriptionId());
        e.setAmount(d.getAmount());
        e.setMethod(d.getMethod() != null ? d.getMethod().name() : null);
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        e.setTransactionId(d.getTransactionId());
        e.setGatewayRef(d.getGatewayRef());
        e.setTransferReference(d.getTransferReference());
        e.setExpiresAt(d.getExpiresAt());
        e.setPaidAt(d.getPaidAt());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
