package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;

public interface SearchBooksUseCase {
    PagedResponse<BookResponse> search(String keyword, int page, int size);
}
