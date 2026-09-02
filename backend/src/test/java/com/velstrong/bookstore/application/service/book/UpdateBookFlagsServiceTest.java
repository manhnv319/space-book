package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.command.book.UpdateBookFlagsCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateBookFlagsServiceTest {

    @Test
    void updatesFlagsWhenBookExists() {
        BookRepository bookRepository = mock(BookRepository.class);
        UpdateBookFlagsService service = new UpdateBookFlagsService(bookRepository);
        when(bookRepository.updateFlags(1L, true, false)).thenReturn(true);

        service.updateFlags(new UpdateBookFlagsCommand(1L, true, false));

        verify(bookRepository).updateFlags(1L, true, false);
    }

    @Test
    void throwsNotFoundWhenBookDoesNotExist() {
        BookRepository bookRepository = mock(BookRepository.class);
        UpdateBookFlagsService service = new UpdateBookFlagsService(bookRepository);
        when(bookRepository.updateFlags(99L, true, true)).thenReturn(false);

        assertThatThrownBy(() -> service.updateFlags(new UpdateBookFlagsCommand(99L, true, true)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
