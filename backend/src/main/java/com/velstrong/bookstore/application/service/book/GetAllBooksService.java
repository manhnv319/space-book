package com.velstrong.bookstore.application.service.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.port.in.book.GetAllBooksUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;

@Service
@Transactional(readOnly = true)
public class GetAllBooksService implements GetAllBooksUseCase {

    private final BookRepository bookRepository;

    public GetAllBooksService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PagedResponse<BookResponse> getAll(int page, int size, String sortBy, boolean asc) {
        var result = bookRepository.findAll(page, size, sortBy, asc);
        return PagedResponse.of(
                result.content().stream().map(BookResponse::from).toList(),
                page, size, result.totalElements());
    }
}
