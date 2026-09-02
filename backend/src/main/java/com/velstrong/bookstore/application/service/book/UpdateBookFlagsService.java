package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.command.book.UpdateBookFlagsCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.book.UpdateBookFlagsUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateBookFlagsService implements UpdateBookFlagsUseCase {

    private final BookRepository bookRepository;

    public UpdateBookFlagsService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void updateFlags(UpdateBookFlagsCommand command) {
        boolean updated = bookRepository.updateFlags(
                command.bookId(), command.isFeatured(), command.isBestseller());
        if (!updated) {
            throw new EntityNotFoundException("Book", command.bookId());
        }
    }
}
