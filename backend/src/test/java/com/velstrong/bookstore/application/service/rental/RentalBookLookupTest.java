package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * A rental points at a copy, and the title is two tables further on. Without
 * this the rentals screen can only say "copy #12", which tells a reader nothing.
 */
class RentalBookLookupTest {

    private BookCopyRepository copies;
    private BookRepository books;
    private RentalBookLookup lookup;

    @BeforeEach
    void setUp() {
        copies = mock(BookCopyRepository.class);
        books = mock(BookRepository.class);
        lookup = new RentalBookLookup(copies, books);
    }

    private Rental rentalOnCopy(Long id, Long copyId) {
        Rental rental = mock(Rental.class);
        when(rental.getId()).thenReturn(id);
        when(rental.getBookCopyId()).thenReturn(copyId);
        return rental;
    }

    private BookCopy copy(Long id, Long bookId) {
        BookCopy value = mock(BookCopy.class);
        when(value.getId()).thenReturn(id);
        when(value.getBookId()).thenReturn(bookId);
        return value;
    }

    private Book book(Long id, String title) {
        Book value = mock(Book.class);
        when(value.getId()).thenReturn(id);
        when(value.getTitle()).thenReturn(title);
        return value;
    }

    @Test
    void attachesTheTitleThroughTheCopy() {
        // Dựng mock trước: gọi when() lồng trong when() làm Mockito hiểu nhầm.
        List<BookCopy> foundCopies = List.of(copy(12L, 5L));
        List<Book> foundBooks = List.of(book(5L, "Nhà Giả Kim"));
        when(copies.findByIds(anyList())).thenReturn(foundCopies);
        when(books.findByIds(anyList())).thenReturn(foundBooks);

        RentalResponse enriched = lookup.enrich(rentalOnCopy(1L, 12L));

        assertThat(enriched.bookId()).isEqualTo(5L);
        assertThat(enriched.bookTitle()).isEqualTo("Nhà Giả Kim");
    }

    @Test
    void readsEachTableOnceForAWholePage() {
        List<BookCopy> foundCopies = List.of(copy(12L, 5L), copy(13L, 5L), copy(14L, 6L));
        List<Book> foundBooks = List.of(book(5L, "A"), book(6L, "B"));
        when(copies.findByIds(anyList())).thenReturn(foundCopies);
        when(books.findByIds(anyList())).thenReturn(foundBooks);

        List<Rental> rentals = List.of(rentalOnCopy(1L, 12L), rentalOnCopy(2L, 13L), rentalOnCopy(3L, 14L));
        lookup.enrich(rentals);

        // Three rentals must not become six round trips.
        verify(copies, times(1)).findByIds(anyList());
        verify(books, times(1)).findByIds(anyList());

        ArgumentCaptor<List<Long>> askedForBooks = ArgumentCaptor.captor();
        verify(books).findByIds(askedForBooks.capture());
        assertThat(askedForBooks.getValue()).containsExactlyInAnyOrder(5L, 6L);
    }

    @Test
    void keepsTheRentalWhenTheCopyOrBookIsGone() {
        // The reader still has a deposit tied up; dropping the row would hide it.
        when(copies.findByIds(anyList())).thenReturn(List.of());
        when(books.findByIds(anyList())).thenReturn(List.of());

        RentalResponse enriched = lookup.enrich(rentalOnCopy(1L, 12L));

        assertThat(enriched.id()).isEqualTo(1L);
        assertThat(enriched.bookTitle()).isNull();
    }

    @Test
    void touchesNothingForAnEmptyPage() {
        assertThat(lookup.enrich(List.of())).isEmpty();
        verifyNoInteractions(copies, books);
    }
}
