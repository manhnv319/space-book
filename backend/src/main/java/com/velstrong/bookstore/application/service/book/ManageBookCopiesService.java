package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.command.book.CreateBookCopyCommand;
import com.velstrong.bookstore.application.command.book.UpdateBookCopyCommand;
import com.velstrong.bookstore.application.response.book.BookCopyResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.port.in.book.ManageBookCopiesUseCase;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ManageBookCopiesService implements ManageBookCopiesUseCase {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public ManageBookCopiesService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponse> getByBookId(Long bookId) {
        requireBook(bookId);
        return bookCopyRepository.findByBookId(bookId).stream().map(BookCopyResponse::from).toList();
    }

    @Override
    public BookCopyResponse create(CreateBookCopyCommand command) {
        requireBook(command.bookId());
        BookCopy copy = BookCopy.create(command.bookId(), command.condition());
        return BookCopyResponse.from(bookCopyRepository.save(copy));
    }

    @Override
    public BookCopyResponse update(UpdateBookCopyCommand command) {
        BookCopy copy = bookCopyRepository.findById(command.copyId())
                .orElseThrow(() -> new EntityNotFoundException("BookCopy", command.copyId()));
        copy.updateManagement(command.status(), command.condition(), command.notes());
        return BookCopyResponse.from(bookCopyRepository.save(copy));
    }

    private void requireBook(Long bookId) {
        if (bookId == null || bookRepository.findById(bookId).isEmpty()) {
            throw new EntityNotFoundException("Book", bookId);
        }
    }
}
