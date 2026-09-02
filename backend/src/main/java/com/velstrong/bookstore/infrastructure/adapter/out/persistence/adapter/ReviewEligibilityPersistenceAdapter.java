package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.ReviewableOrderItem;
import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;
import com.velstrong.bookstore.domain.port.out.ReviewEligibilityRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaOrderItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaRentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component
@Profile("postgres & !mongodb")
public class ReviewEligibilityPersistenceAdapter implements ReviewEligibilityRepository {
    private final JpaOrderItemRepository orderItems;
    private final JpaRentalRepository rentals;

    public ReviewEligibilityPersistenceAdapter(JpaOrderItemRepository orderItems, JpaRentalRepository rentals) {
        this.orderItems = orderItems; this.rentals = rentals;
    }

    @Override
    public Optional<ReviewableOrderItem> findReviewable(Long userId, Long bookId, Long orderItemId) {
        return orderItems.findPaidForReview(userId, bookId, orderItemId).flatMap(item -> {
            if ("PURCHASE".equals(item.getItemType())) return Optional.of(new ReviewableOrderItem(item.getId(), ReviewSource.PURCHASE));
            if ("RENTAL".equals(item.getItemType()) && rentals.existsByOrderItemId(item.getId()))
                return Optional.of(new ReviewableOrderItem(item.getId(), ReviewSource.RENTAL));
            return Optional.empty();
        });
    }

    @Override
    public List<ReviewableOrderItem> findReviewable(Long userId, Long bookId) {
        return orderItems.findPaidItemsForReview(userId, bookId).stream().map(item -> {
            if ("PURCHASE".equals(item.getItemType())) return new ReviewableOrderItem(item.getId(), ReviewSource.PURCHASE);
            return rentals.existsByOrderItemId(item.getId()) ? new ReviewableOrderItem(item.getId(), ReviewSource.RENTAL) : null;
        }).filter(java.util.Objects::nonNull).toList();
    }
}
