package com.velstrong.bookstore.application.response.category;

import com.velstrong.bookstore.domain.model.Category;

public record CategoryResponse(
        Long id,
        String name,
        String slug
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.slug());
    }
}
