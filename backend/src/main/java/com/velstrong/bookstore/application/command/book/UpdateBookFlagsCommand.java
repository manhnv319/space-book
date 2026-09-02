package com.velstrong.bookstore.application.command.book;

public record UpdateBookFlagsCommand(Long bookId, Boolean isFeatured, Boolean isBestseller) {
}
