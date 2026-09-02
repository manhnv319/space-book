package com.velstrong.bookstore.domain.model.enums.blog;

public enum BlogPostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED;

    public boolean isPublished() {
        return this == PUBLISHED;
    }
}
