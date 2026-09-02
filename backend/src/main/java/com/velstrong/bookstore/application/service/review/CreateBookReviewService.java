package com.velstrong.bookstore.application.service.review;

import com.velstrong.bookstore.application.command.review.CreateBookReviewCommand;
import com.velstrong.bookstore.application.response.review.BookReviewResponse;
import com.velstrong.bookstore.domain.exception.DuplicateEntityException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.BookReview;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import com.velstrong.bookstore.domain.port.out.ReviewEligibilityRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateBookReviewService {
    private final BookReviewRepository reviews;
    private final ReviewEligibilityRepository eligibility;
    public CreateBookReviewService(BookReviewRepository reviews, ReviewEligibilityRepository eligibility) { this.reviews = reviews; this.eligibility = eligibility; }
    public BookReviewResponse create(CreateBookReviewCommand command) {
        if (reviews.findByUserIdAndOrderItemId(command.userId(), command.orderItemId()).isPresent())
            throw new DuplicateEntityException("BookReview", "orderItemId", command.orderItemId());
        var item = eligibility.findReviewable(command.userId(), command.bookId(), command.orderItemId())
                .orElseThrow(() -> new InvalidOperationException("This transaction is not eligible for review"));
        return BookReviewResponse.from(reviews.save(BookReview.create(command.bookId(), command.userId(), item.orderItemId(), item.source(), command.rating(), command.comment())));
    }
}
