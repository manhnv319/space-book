package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;

public interface GetAllBooksUseCase {
    PagedResponse<BookResponse> getAll(int page, int size, String sortBy, boolean asc);
}
