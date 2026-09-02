package com.velstrong.bookstore.application.service.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.book.GetBookUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;

@Service
@Transactional(readOnly = true)
public class GetBookService implements GetBookUseCase {

    private final BookRepository bookRepository;

    public GetBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookResponse getById(Long bookId) {
        return bookRepository.findById(bookId)
                .map(BookResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
    }
}
