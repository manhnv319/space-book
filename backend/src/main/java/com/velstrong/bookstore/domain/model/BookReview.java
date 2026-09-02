package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;

import java.time.LocalDateTime;

public class BookReview {
    private final Long id;
    private final Long bookId;
    private final Long userId;
    private final Long orderItemId;
    private final ReviewSource source;
    private int rating;
    private String comment;
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private BookReview(Long id, Long bookId, Long userId, Long orderItemId, ReviewSource source,
                       int rating, String comment, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id; this.bookId = bookId; this.userId = userId; this.orderItemId = orderItemId;
        this.source = source; this.rating = rating; this.comment = comment;
        this.createdAt = createdAt; this.modifiedAt = modifiedAt;
    }

    public static BookReview create(Long bookId, Long userId, Long orderItemId, ReviewSource source,
                                    int rating, String comment) {
        validate(bookId, userId, orderItemId, source, rating, comment);
        LocalDateTime now = LocalDateTime.now();
        return new BookReview(null, bookId, userId, orderItemId, source, rating, normalize(comment), now, now);
    }

    public static BookReview reconstitute(Long id, Long bookId, Long userId, Long orderItemId, ReviewSource source,
                                          int rating, String comment, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new BookReview(id, bookId, userId, orderItemId, source, rating, comment, createdAt, modifiedAt);
    }

    public void update(int rating, String comment) {
        validate(bookId, userId, orderItemId, source, rating, comment);
        this.rating = rating; this.comment = normalize(comment); this.modifiedAt = LocalDateTime.now();
    }

    private static void validate(Long bookId, Long userId, Long orderItemId, ReviewSource source, int rating, String comment) {
        if (bookId == null || userId == null || orderItemId == null || source == null) throw new InvalidOperationException("Review identity is invalid");
        if (rating < 1 || rating > 5) throw new InvalidOperationException("Rating must be between 1 and 5");
        String normalized = normalize(comment);
        if (normalized.isEmpty() || normalized.length() > 2000) throw new InvalidOperationException("Review comment must contain 1 to 2000 characters");
    }

    private static String normalize(String comment) { return comment == null ? "" : comment.trim(); }
    public Long getId() { return id; } public Long getBookId() { return bookId; } public Long getUserId() { return userId; }
    public Long getOrderItemId() { return orderItemId; } public ReviewSource getSource() { return source; }
    public int getRating() { return rating; } public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; } public LocalDateTime getModifiedAt() { return modifiedAt; }
}
