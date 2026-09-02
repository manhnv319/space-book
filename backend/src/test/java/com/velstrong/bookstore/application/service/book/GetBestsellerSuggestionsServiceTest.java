package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.BookSalesCount;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetBestsellerSuggestionsServiceTest {

    @Test
    void keepsSalesDescendingOrderFromAggregateQuery() {
        OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        GetBestsellerSuggestionsService service =
                new GetBestsellerSuggestionsService(orderItemRepository, bookRepository);

        when(orderItemRepository.findTopSellingBooks(any(), eq("PURCHASE"), eq(20)))
                .thenReturn(List.of(new BookSalesCount(2L, 50L), new BookSalesCount(1L, 30L)));
        when(bookRepository.findByIds(List.of(2L, 1L)))
                .thenReturn(List.of(book(1L, "Book A", false, false), book(2L, "Book B", true, true)));

        var result = service.getSuggestions(20, 90, "PURCHASE");

        assertThat(result).extracting("bookId", "soldQuantity")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(2L, 50L),
                        org.assertj.core.groups.Tuple.tuple(1L, 30L));
        verify(orderItemRepository).findTopSellingBooks(any(LocalDateTime.class), eq("PURCHASE"), eq(20));
    }

    @Test
    void skipsSaleWhenBookNoLongerExists() {
        OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        GetBestsellerSuggestionsService service =
                new GetBestsellerSuggestionsService(orderItemRepository, bookRepository);

        when(orderItemRepository.findTopSellingBooks(any(), eq((String) null), anyInt()))
                .thenReturn(List.of(new BookSalesCount(1L, 10L), new BookSalesCount(99L, 5L)));
        when(bookRepository.findByIds(List.of(1L, 99L)))
                .thenReturn(List.of(book(1L, "Book A", false, false)));

        var result = service.getSuggestions(20, 90, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().bookId()).isEqualTo(1L);
    }

    @Test
    void returnsEmptyWhenNoSales() {
        OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        GetBestsellerSuggestionsService service =
                new GetBestsellerSuggestionsService(orderItemRepository, bookRepository);

        when(orderItemRepository.findTopSellingBooks(any(), eq((String) null), anyInt()))
                .thenReturn(List.of());

        var result = service.getSuggestions(20, 90, null);

        assertThat(result).isEmpty();
        verify(bookRepository, org.mockito.Mockito.never()).findByIds(any());
    }

    private Book book(Long id, String title, boolean isFeatured, boolean isBestseller) {
        return Book.reconstitute(id, "ISBN-" + id, title, "desc", null, null,
                100_000L, null, null, null, null, (short) 2020, "Pub", "vi", (short) 300,
                true, List.of(), List.of(), null, isFeatured, isBestseller);
    }
}
