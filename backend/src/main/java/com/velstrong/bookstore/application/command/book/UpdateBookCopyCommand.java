package com.velstrong.bookstore.application.command.book;

import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;

public record UpdateBookCopyCommand(Long copyId, BookCopyStatus status, BookCopyCondition condition, String notes) {}
