package com.velstrong.bookstore.application.response.book;

public record BestsellerSuggestionResponse(
        Long bookId,
        String title,
        Long soldQuantity,
        Boolean isFeatured,
        Boolean isBestseller
) {
}
