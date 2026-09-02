package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;

public interface GetBookShelfUseCase {
    PagedResponse<BookResponse> getShelf(BookShelf shelf, int page, int size);
}
