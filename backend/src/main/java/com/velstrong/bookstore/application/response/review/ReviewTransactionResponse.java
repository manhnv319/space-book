package com.velstrong.bookstore.application.response.review;

public record ReviewTransactionResponse(Long orderItemId, String source, BookReviewResponse review) { }
