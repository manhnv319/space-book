package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookCategoryId implements Serializable {
    private Long bookId;
    private Long categoryId;

    public BookCategoryId() {
    }

    public BookCategoryId(Long bookId, Long categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCategoryId that)) return false;
        return Objects.equals(bookId, that.bookId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, categoryId);
    }
}
