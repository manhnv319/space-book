package com.velstrong.bookstore.application.response.review;

import com.velstrong.bookstore.domain.model.BookReview;

import java.time.LocalDateTime;

public record BookReviewResponse(Long id, Long bookId, Long orderItemId, String source, int rating,
                                 String comment, LocalDateTime createdAt, LocalDateTime modifiedAt) {
    public static BookReviewResponse from(BookReview review) {
        return new BookReviewResponse(review.getId(), review.getBookId(), review.getOrderItemId(), review.getSource().name(),
                review.getRating(), review.getComment(), review.getCreatedAt(), review.getModifiedAt());
    }
}
