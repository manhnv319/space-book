package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.BookReview;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookReviewJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaBookReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component
@Profile("postgres & !mongodb")
public class BookReviewPersistenceAdapter implements BookReviewRepository {
    private final JpaBookReviewRepository repository;
    public BookReviewPersistenceAdapter(JpaBookReviewRepository repository) { this.repository = repository; }
    @Override public BookReview save(BookReview review) { return toDomain(repository.save(toEntity(review))); }
    @Override public Optional<BookReview> findById(Long id) { return repository.findById(id).map(this::toDomain); }
    @Override public Optional<BookReview> findByUserIdAndOrderItemId(Long userId, Long orderItemId) { return repository.findByUserIdAndOrderItemId(userId, orderItemId).map(this::toDomain); }
    @Override public List<BookReview> findByUserIdAndBookId(Long userId, Long bookId) { return repository.findByUserIdAndBookIdOrderByCreatedAtDesc(userId, bookId).stream().map(this::toDomain).toList(); }
    @Override public PageResult<BookReview> findByBookId(Long bookId, int page, int size) {
        Page<BookReviewJpaEntity> result = repository.findByBookIdOrderByCreatedAtDesc(bookId, PageRequest.of(page, size));
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }
    private BookReview toDomain(BookReviewJpaEntity e) { return BookReview.reconstitute(e.getId(), e.getBookId(), e.getUserId(), e.getOrderItemId(), ReviewSource.valueOf(e.getSource()), e.getRating(), e.getComment(), e.getCreatedAt(), e.getModifiedAt()); }
    private BookReviewJpaEntity toEntity(BookReview d) {
        BookReviewJpaEntity e = new BookReviewJpaEntity(); e.setId(d.getId()); e.setBookId(d.getBookId()); e.setUserId(d.getUserId()); e.setOrderItemId(d.getOrderItemId()); e.setSource(d.getSource().name()); e.setRating((short) d.getRating()); e.setComment(d.getComment()); e.setCreatedAt(d.getCreatedAt()); e.setModifiedAt(d.getModifiedAt()); return e;
    }
}
