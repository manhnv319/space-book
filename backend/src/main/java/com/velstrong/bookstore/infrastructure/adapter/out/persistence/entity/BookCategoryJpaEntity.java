package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_categories")
@IdClass(BookCategoryId.class)
@Getter
@Setter
public class BookCategoryJpaEntity {

    @Id
    private Long bookId;

    @Id
    private Long categoryId;
}
