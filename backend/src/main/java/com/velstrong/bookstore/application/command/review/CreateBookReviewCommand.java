package com.velstrong.bookstore.application.command.review;

public record CreateBookReviewCommand(Long userId, Long bookId, Long orderItemId, int rating, String comment) { }
