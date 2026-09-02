package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaOrderRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.OrderMapper;
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
public class OrderPersistenceAdapter implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.mapper = new OrderMapper();
    }

    @Override
    /**
     * Nạp bản ghi đang được quản lý rồi cập nhật lên đó, thay vì dựng entity mới
     * có sẵn id.
     *
     * `OrderJpaEntity` có `@Version`. Entity dựng tay có id nhưng version null là
     * detached với optimistic lock chưa khởi tạo — Hibernate từ chối lưu, nên mọi
     * lần CẬP NHẬT đơn (đánh dấu đã trả tiền, huỷ đơn, đổi trạng thái) đều hỏng.
     */
    public Order save(Order order) {
        OrderJpaEntity entity = order.getId() == null ? new OrderJpaEntity()
                : jpaOrderRepository.findById(order.getId()).orElseGet(OrderJpaEntity::new);
        return mapper.toDomain(jpaOrderRepository.save(mapper.applyTo(entity, order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdForUpdate(Long id) {
        return jpaOrderRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderCode(String orderCode) {
        return jpaOrderRepository.findByOrderCode(orderCode).map(mapper::toDomain);
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, OrderStatus status, PaymentStatus paymentStatus,
                                           int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderJpaEntity> result = jpaOrderRepository.findByUserIdWithFilters(
                userId, status != null ? status.name() : null,
                paymentStatus != null ? paymentStatus.name() : null, pageable);
        return PageResult.of(result.map(mapper::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public PageResult<Order> findAll(OrderStatus status, PaymentStatus paymentStatus, int page, int size,
                                     LocalDate fromDate, LocalDate toDate, String search) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderJpaEntity> result = jpaOrderRepository.findAllWithFilters(
                status != null ? status.name() : null, paymentStatus != null ? paymentStatus.name() : null,
                search, toDateTime(fromDate, false), toDateTime(toDate, true), pageable);
        return PageResult.of(result.map(mapper::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public boolean existsByOrderCode(String orderCode) {
        return jpaOrderRepository.existsByOrderCode(orderCode);
    }

    private static LocalDateTime toDateTime(LocalDate date, boolean endOfDay) {
        if (date == null) return null;
        return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
    }

    @Override
    public java.util.List<Order> findAdvanceable(
            java.util.List<com.velstrong.bookstore.domain.model.enums.order.OrderStatus> statuses,
            java.time.LocalDateTime cutoff) {
        java.util.List<String> names = statuses.stream().map(Enum::name).toList();
        return jpaOrderRepository.findAdvanceable(names, cutoff).stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<Order> findByUserIdAndStatuses(Long userId,
            java.util.List<com.velstrong.bookstore.domain.model.enums.order.OrderStatus> statuses,
            int page, int size) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<
                com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity> found =
                statuses == null || statuses.isEmpty()
                        ? jpaOrderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                        : jpaOrderRepository.findByUserIdAndStatusInOrderByCreatedAtDesc(userId,
                                statuses.stream().map(Enum::name).toList(), pageable);
        return PageResult.of(found.getContent().stream().map(mapper::toDomain).toList(), found.getTotalElements());
    }

    @Override
    public java.util.Map<com.velstrong.bookstore.domain.model.enums.order.OrderStatus, Long>
            countByStatusForUser(Long userId) {
        java.util.Map<com.velstrong.bookstore.domain.model.enums.order.OrderStatus, Long> counts =
                new java.util.EnumMap<>(com.velstrong.bookstore.domain.model.enums.order.OrderStatus.class);
        for (Object[] row : jpaOrderRepository.countByStatus(userId)) {
            counts.put(com.velstrong.bookstore.domain.model.enums.order.OrderStatus.valueOf((String) row[0]),
                    (Long) row[1]);
        }
        return counts;
    }
}
