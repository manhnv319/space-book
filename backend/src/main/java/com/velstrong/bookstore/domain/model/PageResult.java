package com.velstrong.bookstore.domain.model;

import java.util.List;

/**
 * Generic page wrapper returned by repository ports.
 * Collapses the content + count into a single round-trip by
 * piggy-backing on Spring Data's {@code Page.getTotalElements()}.
 */
public record PageResult<T>(List<T> content, long totalElements) {

    public static <T> PageResult<T> of(List<T> content, long totalElements) {
        return new PageResult<>(content, totalElements);
    }
}
