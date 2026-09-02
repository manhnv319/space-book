package com.velstrong.bookstore.application.service.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.port.in.book.SearchBooksUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;

@Service
@Transactional(readOnly = true)
public class SearchBooksService implements SearchBooksUseCase {

    private final BookRepository bookRepository;

    public SearchBooksService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PagedResponse<BookResponse> search(String keyword, int page, int size) {
        var result = bookRepository.searchByTitle(keyword, page, size);
        return PagedResponse.of(
                result.content().stream().map(BookResponse::from).toList(),
                page, size, result.totalElements());
    }
}
