package com.velstrong.bookstore.application.service.review;

import com.velstrong.bookstore.application.response.review.BookReviewResponse;
import com.velstrong.bookstore.application.response.review.ReviewTransactionResponse;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import com.velstrong.bookstore.domain.port.out.ReviewEligibilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetMyBookReviewOptionsService {
    private final ReviewEligibilityRepository eligibility;
    private final BookReviewRepository reviews;
    public GetMyBookReviewOptionsService(ReviewEligibilityRepository eligibility, BookReviewRepository reviews) { this.eligibility = eligibility; this.reviews = reviews; }
    public List<ReviewTransactionResponse> get(Long userId, Long bookId) {
        Map<Long, BookReviewResponse> existing = reviews.findByUserIdAndBookId(userId, bookId).stream().collect(Collectors.toMap(r -> r.getOrderItemId(), BookReviewResponse::from, (a, b) -> a));
        return eligibility.findReviewable(userId, bookId).stream().map(item -> new ReviewTransactionResponse(item.orderItemId(), item.source().name(), existing.get(item.orderItemId()))).toList();
    }
}
