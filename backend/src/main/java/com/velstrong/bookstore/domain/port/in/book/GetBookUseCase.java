package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BookResponse;

public interface GetBookUseCase {
    BookResponse getById(Long bookId);
}
