package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;
import com.velstrong.bookstore.domain.port.in.book.GetBookShelfUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetBookShelfService implements GetBookShelfUseCase {

    private final BookRepository bookRepository;

    public GetBookShelfService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PagedResponse<BookResponse> getShelf(BookShelf shelf, int page, int size) {
        var result = bookRepository.findByShelf(shelf, page, size);
        return PagedResponse.of(
                result.content().stream().map(BookResponse::from).toList(),
                page, size, result.totalElements());
    }
}
