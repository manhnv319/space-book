package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookCategoryId;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookCategoryRepository extends JpaRepository<BookCategoryJpaEntity, BookCategoryId> {
}
