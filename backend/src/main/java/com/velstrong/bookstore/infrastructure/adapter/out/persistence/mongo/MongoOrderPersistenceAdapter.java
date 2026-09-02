package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.OrderMapper;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("mongodb & !postgres")
public class MongoOrderPersistenceAdapter extends MongoPersistenceSupport implements OrderRepository {

    private static final String COLLECTION = "orders";
    private final OrderMapper mapper = new OrderMapper();

    public MongoOrderPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override
    public List<Order> findAdvanceable(List<OrderStatus> statuses, LocalDateTime cutoff) {
        if (statuses == null || statuses.isEmpty()) return List.of();
        Query query = Query.query(new Criteria().andOperator(Criteria.where("status").in(statuses.stream().map(Enum::name).toList()),
                Criteria.where("paymentStatus").is(PaymentStatus.PAID.name()), Criteria.where("modifiedAt").lt(cutoff)));
        return find(COLLECTION, OrderJpaEntity.class, query).stream().map(mapper::toDomain).toList();
    }

    @Override public Order save(Order order) { return mapper.toDomain(save(COLLECTION, mapper.applyTo(entityFor(order), order))); }
    @Override public Optional<Order> findById(Long id) { return findById(COLLECTION, OrderJpaEntity.class, id).map(mapper::toDomain); }
    @Override
    public Optional<Order> findByIdForUpdate(Long id) {
        return findAndModify(COLLECTION, OrderJpaEntity.class,
                Query.query(Criteria.where("_id").is(id)),
                new Update().set("_mongoLock", UUID.randomUUID().toString())).map(mapper::toDomain);
    }
    @Override public Optional<Order> findByOrderCode(String orderCode) { return findOne(COLLECTION, OrderJpaEntity.class, Query.query(Criteria.where("orderCode").is(orderCode))).map(mapper::toDomain); }

    @Override
    public PageResult<Order> findByUserIdAndStatuses(Long userId, List<OrderStatus> statuses, int page, int size) {
        Criteria user = Criteria.where("userId").is(userId);
        Criteria criteria = statuses == null || statuses.isEmpty() ? user
                : new Criteria().andOperator(user, Criteria.where("status").in(statuses.stream().map(Enum::name).toList()));
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override
    public Map<OrderStatus, Long> countByStatusForUser(Long userId) {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        mongo.find(Query.query(Criteria.where("userId").is(userId)), Document.class, COLLECTION)
                .forEach(value -> {
                    String status = value.getString("status");
                    if (status != null) counts.merge(OrderStatus.valueOf(status), 1L, Long::sum);
                });
        return counts;
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, OrderStatus status, PaymentStatus paymentStatus, int page, int size) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (status != null) criteria = new Criteria().andOperator(criteria, Criteria.where("status").is(status.name()));
        if (paymentStatus != null) criteria = new Criteria().andOperator(criteria, Criteria.where("paymentStatus").is(paymentStatus.name()));
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override
    public PageResult<Order> findAll(OrderStatus status, PaymentStatus paymentStatus, int page, int size,
                                     LocalDate fromDate, LocalDate toDate, String search) {
        Criteria criteria = new Criteria();
        if (status != null) criteria = new Criteria().andOperator(criteria, Criteria.where("status").is(status.name()));
        if (paymentStatus != null) criteria = new Criteria().andOperator(criteria, Criteria.where("paymentStatus").is(paymentStatus.name()));
        if (search != null && !search.isBlank()) criteria = new Criteria().andOperator(criteria,
                Criteria.where("orderCode").regex(java.util.regex.Pattern.quote(search.trim()), "i"));
        if (fromDate != null) criteria = new Criteria().andOperator(criteria, Criteria.where("createdAt").gte(fromDate.atStartOfDay()));
        if (toDate != null) criteria = new Criteria().andOperator(criteria, Criteria.where("createdAt").lte(toDate.atTime(23, 59, 59)));
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override public boolean existsByOrderCode(String orderCode) { return exists(COLLECTION, Query.query(Criteria.where("orderCode").is(orderCode)), OrderJpaEntity.class); }

    private PageResult<Order> toPage(Query query, int page, int size) {
        List<Order> values = find(COLLECTION, OrderJpaEntity.class, query.limit(size).skip((long) page * size))
                .stream().map(mapper::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), OrderJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    private OrderJpaEntity entityFor(Order order) {
        if (order.getId() == null) return new OrderJpaEntity();
        return findById(COLLECTION, OrderJpaEntity.class, order.getId()).orElseGet(OrderJpaEntity::new);
    }
}
