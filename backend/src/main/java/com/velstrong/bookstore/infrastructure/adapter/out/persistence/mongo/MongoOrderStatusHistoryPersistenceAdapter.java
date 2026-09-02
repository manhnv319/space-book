package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderStatusHistoryJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("mongodb & !postgres")
public class MongoOrderStatusHistoryPersistenceAdapter extends MongoPersistenceSupport implements OrderStatusHistoryRepository {

    private static final String COLLECTION = "order_status_history";

    public MongoOrderStatusHistoryPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override
    public void record(Long orderId, OrderStatus status, String source, LocalDateTime changedAt) {
        OrderStatusHistoryJpaEntity entity = new OrderStatusHistoryJpaEntity();
        entity.setOrderId(orderId); entity.setStatus(status.name()); entity.setSource(source); entity.setChangedAt(changedAt);
        save(COLLECTION, entity);
    }

    @Override
    public List<OrderStatusChange> findByOrderId(Long orderId) {
        return find(COLLECTION, OrderStatusHistoryJpaEntity.class,
                Query.query(Criteria.where("orderId").is(orderId)).with(Sort.by(Sort.Direction.ASC, "changedAt")))
                .stream().map(value -> new OrderStatusChange(value.getId(), value.getOrderId(),
                        OrderStatus.valueOf(value.getStatus()), value.getSource(), value.getChangedAt())).toList();
    }
}
