package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.command.book.CreateBookCopyCommand;
import com.velstrong.bookstore.application.command.book.UpdateBookCopyCommand;
import com.velstrong.bookstore.application.response.book.BookCopyResponse;

import java.util.List;

public interface ManageBookCopiesUseCase {
    List<BookCopyResponse> getByBookId(Long bookId);
    BookCopyResponse create(CreateBookCopyCommand command);
    BookCopyResponse update(UpdateBookCopyCommand command);
}
