package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.port.in.book.GetBooksByCategoryUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetBooksByCategoryService implements GetBooksByCategoryUseCase {

    private final BookRepository bookRepository;

    public GetBooksByCategoryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PagedResponse<BookResponse> getByCategories(List<Long> categoryIds, int page, int size) {
        var result = bookRepository.findByCategories(categoryIds, page, size);
        return PagedResponse.of(
                result.content().stream().map(BookResponse::from).toList(),
                page, size, result.totalElements());
    }
}
