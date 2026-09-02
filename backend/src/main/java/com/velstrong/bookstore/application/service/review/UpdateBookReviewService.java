package com.velstrong.bookstore.application.service.review;

import com.velstrong.bookstore.application.command.review.CreateBookReviewCommand;
import com.velstrong.bookstore.application.response.review.BookReviewResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateBookReviewService {
    private final BookReviewRepository reviews;
    public UpdateBookReviewService(BookReviewRepository reviews) { this.reviews = reviews; }
    public BookReviewResponse update(Long reviewId, CreateBookReviewCommand command) {
        var review = reviews.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("BookReview", reviewId));
        if (!review.getUserId().equals(command.userId()) || !review.getBookId().equals(command.bookId()))
            throw new InvalidOperationException("You are not authorized to update this review");
        review.update(command.rating(), command.comment());
        return BookReviewResponse.from(reviews.save(review));
    }
}
