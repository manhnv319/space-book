package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import jakarta.validation.constraints.NotNull;

public record UpdateBookFlagsRequest(
        @NotNull Boolean isFeatured,
        @NotNull Boolean isBestseller
) {
}
