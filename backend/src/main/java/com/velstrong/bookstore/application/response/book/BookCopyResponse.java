package com.velstrong.bookstore.application.response.book;

import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;

public record BookCopyResponse(Long id, Long bookId, BookCopyStatus status, BookCopyCondition condition, String notes) {
    public static BookCopyResponse from(BookCopy copy) {
        return new BookCopyResponse(copy.getId(), copy.getBookId(), copy.getStatus(), copy.getCondition(), copy.getNotes());
    }
}
