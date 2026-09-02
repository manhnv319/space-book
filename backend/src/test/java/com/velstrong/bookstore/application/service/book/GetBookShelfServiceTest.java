package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetBookShelfServiceTest {

    @Test
    void getShelfDelegatesToRepositoryAndMapsToBookResponse() {
        BookRepository bookRepository = mock(BookRepository.class);
        GetBookShelfService service = new GetBookShelfService(bookRepository);

        Book book = Book.reconstitute(1L, "ISBN", "Clean Code", "desc", null, null,
                100_000L, null, null, null, null, (short) 2020, "Pub", "vi", (short) 300,
                true, List.of(), List.of(), null, true, false);

        when(bookRepository.findByShelf(eq(BookShelf.FEATURED), eq(0), eq(20)))
                .thenReturn(PageResult.of(List.of(book), 1));

        var result = service.getShelf(BookShelf.FEATURED, 0, 20);

        verify(bookRepository).findByShelf(BookShelf.FEATURED, 0, 20);
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().title()).isEqualTo("Clean Code");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getShelfSupportsAllThreeShelves() {
        BookRepository bookRepository = mock(BookRepository.class);
        GetBookShelfService service = new GetBookShelfService(bookRepository);
        when(bookRepository.findByShelf(org.mockito.ArgumentMatchers.any(), eq(0), eq(20)))
                .thenReturn(PageResult.of(List.of(), 0));

        service.getShelf(BookShelf.BESTSELLER, 0, 20);
        service.getShelf(BookShelf.NEW_ARRIVAL, 0, 20);

        verify(bookRepository).findByShelf(BookShelf.BESTSELLER, 0, 20);
        verify(bookRepository).findByShelf(BookShelf.NEW_ARRIVAL, 0, 20);
    }
}
