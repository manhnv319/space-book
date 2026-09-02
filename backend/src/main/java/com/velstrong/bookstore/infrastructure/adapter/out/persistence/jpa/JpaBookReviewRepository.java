package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookReviewJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface JpaBookReviewRepository extends JpaRepository<BookReviewJpaEntity, Long> {
    Optional<BookReviewJpaEntity> findByUserIdAndOrderItemId(Long userId, Long orderItemId);
    List<BookReviewJpaEntity> findByUserIdAndBookIdOrderByCreatedAtDesc(Long userId, Long bookId);
    Page<BookReviewJpaEntity> findByBookIdOrderByCreatedAtDesc(Long bookId, Pageable pageable);
}
