package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.ReviewableOrderItem;

import java.util.Optional;
import java.util.List;

public interface ReviewEligibilityRepository {
    Optional<ReviewableOrderItem> findReviewable(Long userId, Long bookId, Long orderItemId);
    List<ReviewableOrderItem> findReviewable(Long userId, Long bookId);
}
