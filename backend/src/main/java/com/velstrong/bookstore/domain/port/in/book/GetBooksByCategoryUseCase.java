package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;

import java.util.List;

public interface GetBooksByCategoryUseCase {
    PagedResponse<BookResponse> getByCategories(List<Long> categoryIds, int page, int size);
}
