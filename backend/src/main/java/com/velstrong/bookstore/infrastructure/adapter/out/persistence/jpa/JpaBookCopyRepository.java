package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookCopyJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface JpaBookCopyRepository extends JpaRepository<BookCopyJpaEntity, Long> {
    List<BookCopyJpaEntity> findByBookIdAndStatus(Long bookId, String status);
    List<BookCopyJpaEntity> findByBookIdOrderByIdAsc(Long bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookCopyJpaEntity> findFirstByBookIdAndStatusOrderByIdAsc(Long bookId, String status);

    int countByBookIdAndStatus(Long bookId, String status);
}
