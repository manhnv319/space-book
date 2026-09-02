package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.BookSalesCount;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.OrderItemMapper;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("mongodb & !postgres")
public class MongoOrderItemPersistenceAdapter extends MongoPersistenceSupport implements OrderItemRepository {

    private static final String COLLECTION = "order_items";
    private static final String ORDERS = "orders";
    private final OrderItemMapper mapper = new OrderItemMapper();

    public MongoOrderItemPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public OrderItem save(OrderItem value) { return mapper.toDomain(save(COLLECTION, mapper.toJpaEntity(value))); }
    @Override public List<OrderItem> saveAll(List<OrderItem> values) { return saveAll(COLLECTION, values.stream().map(mapper::toJpaEntity).toList()).stream().map(mapper::toDomain).toList(); }
    @Override public List<OrderItem> findByOrderId(Long orderId) { return find(COLLECTION, OrderItemJpaEntity.class, Query.query(Criteria.where("orderId").is(orderId))).stream().map(mapper::toDomain).toList(); }

    @Override
    public List<BookSalesCount> findTopSellingBooks(LocalDateTime since, String itemType, int limit) {
        List<Long> validOrderIds = mongo.find(Query.query(new Criteria().andOperator(
                Criteria.where("status").in(List.of("CONFIRMED", "PROCESSING", "SHIPPING", "COMPLETED")),
                Criteria.where("createdAt").gte(since))), Document.class, ORDERS).stream()
                .map(value -> ((Number) value.get("_id")).longValue()).toList();
        if (validOrderIds.isEmpty()) return List.of();
        Criteria criteria = Criteria.where("orderId").in(validOrderIds);
        if (itemType != null) criteria = new Criteria().andOperator(criteria, Criteria.where("itemType").is(itemType));
        Map<Long, Long> quantities = new LinkedHashMap<>();
        find(COLLECTION, OrderItemJpaEntity.class, Query.query(criteria)).forEach(value ->
                quantities.merge(value.getBookId(), value.getQuantity() == null ? 0L : value.getQuantity().longValue(), Long::sum));
        return quantities.entrySet().stream().sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit).map(entry -> new BookSalesCount(entry.getKey(), entry.getValue())).toList();
    }
}
