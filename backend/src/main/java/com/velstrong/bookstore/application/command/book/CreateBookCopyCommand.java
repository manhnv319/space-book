package com.velstrong.bookstore.application.command.book;

import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;

public record CreateBookCopyCommand(Long bookId, BookCopyCondition condition) {}
