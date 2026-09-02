package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.ReviewableOrderItem;
import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;
import com.velstrong.bookstore.domain.port.out.ReviewEligibilityRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalJpaEntity;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoReviewEligibilityPersistenceAdapter extends MongoPersistenceSupport implements ReviewEligibilityRepository {

    private static final String ORDER_ITEMS = "order_items";
    private static final String ORDERS = "orders";
    private static final String RENTALS = "rentals";

    public MongoReviewEligibilityPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override
    public Optional<ReviewableOrderItem> findReviewable(Long userId, Long bookId, Long orderItemId) {
        Criteria itemCriteria = new Criteria().andOperator(Criteria.where("_id").is(orderItemId), Criteria.where("bookId").is(bookId));
        OrderItemJpaEntity item = findOne(ORDER_ITEMS, OrderItemJpaEntity.class, Query.query(itemCriteria)).orElse(null);
        if (item == null || !hasPaidOrder(userId, item.getOrderId())) return Optional.empty();
        if ("PURCHASE".equals(item.getItemType())) return Optional.of(new ReviewableOrderItem(item.getId(), ReviewSource.PURCHASE));
        return rentalExists(item.getId()) ? Optional.of(new ReviewableOrderItem(item.getId(), ReviewSource.RENTAL)) : Optional.empty();
    }

    @Override
    public List<ReviewableOrderItem> findReviewable(Long userId, Long bookId) {
        List<Long> orderIds = mongo.find(Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("paymentStatus").is("PAID"))), Document.class, ORDERS).stream().map(value -> ((Number) value.get("_id")).longValue()).toList();
        if (orderIds.isEmpty()) return List.of();
        return find(ORDER_ITEMS, OrderItemJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("bookId").is(bookId), Criteria.where("orderId").in(orderIds)))).stream().map(item -> {
            if ("PURCHASE".equals(item.getItemType())) return new ReviewableOrderItem(item.getId(), ReviewSource.PURCHASE);
            return rentalExists(item.getId()) ? new ReviewableOrderItem(item.getId(), ReviewSource.RENTAL) : null;
        }).filter(java.util.Objects::nonNull).toList();
    }

    private boolean hasPaidOrder(Long userId, Long orderId) {
        return exists(ORDERS, Query.query(new Criteria().andOperator(Criteria.where("_id").is(orderId), Criteria.where("userId").is(userId), Criteria.where("paymentStatus").is("PAID"))), Document.class);
    }

    private boolean rentalExists(Long orderItemId) { return exists(RENTALS, Query.query(Criteria.where("orderItemId").is(orderItemId)), RentalJpaEntity.class); }
}
