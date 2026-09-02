package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.BookReview;
import com.velstrong.bookstore.domain.model.PageResult;

import java.util.Optional;
import java.util.List;

public interface BookReviewRepository {
    BookReview save(BookReview review);
    Optional<BookReview> findById(Long id);
    Optional<BookReview> findByUserIdAndOrderItemId(Long userId, Long orderItemId);
    List<BookReview> findByUserIdAndBookId(Long userId, Long bookId);
    PageResult<BookReview> findByBookId(Long bookId, int page, int size);
}
