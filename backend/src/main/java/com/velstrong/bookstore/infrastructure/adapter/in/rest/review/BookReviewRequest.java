package com.velstrong.bookstore.infrastructure.adapter.in.rest.review;

import com.velstrong.bookstore.application.command.review.CreateBookReviewCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookReviewRequest(@NotNull Long orderItemId, @Min(1) @Max(5) int rating,
                                @NotBlank @Size(max = 2000) String comment) {
    CreateBookReviewCommand toCommand(Long userId, Long bookId) { return new CreateBookReviewCommand(userId, bookId, orderItemId, rating, comment); }
}
