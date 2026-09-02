package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * An order list carrying only a code and a total gives the customer nothing to
 * recognise — the code is a ULID. Covers and titles are what identify an order.
 */
class OrderItemPreviewLookupTest {

    private OrderItemRepository orderItems;
    private BookRepository books;
    private OrderItemPreviewLookup lookup;

    @BeforeEach
    void setUp() {
        orderItems = mock(OrderItemRepository.class);
        books = mock(BookRepository.class);
        lookup = new OrderItemPreviewLookup(orderItems, books);
    }

    private Order order(Long id) {
        Order value = mock(Order.class);
        when(value.getId()).thenReturn(id);
        return value;
    }

    private OrderItem item(Long bookId, int quantity) {
        OrderItem value = mock(OrderItem.class);
        when(value.getBookId()).thenReturn(bookId);
        when(value.getQuantity()).thenReturn(quantity);
        when(value.getItemType()).thenReturn(ItemType.PURCHASE);
        return value;
    }

    private Book book(Long id, String title) {
        Book value = mock(Book.class);
        when(value.getId()).thenReturn(id);
        when(value.getTitle()).thenReturn(title);
        when(value.getImageUrl()).thenReturn("https://example.test/" + id + ".jpg");
        return value;
    }

    @Test
    void attachesTitleAndCoverToEachOrder() {
        List<OrderItem> items = List.of(item(5L, 2));
        List<Book> found = List.of(book(5L, "Nhà Giả Kim"));
        when(orderItems.findByOrderId(1L)).thenReturn(items);
        when(books.findByIds(anyList())).thenReturn(found);

        OrderResponse enriched = lookup.enrich(List.of(order(1L))).getFirst();

        assertThat(enriched.items()).hasSize(1);
        assertThat(enriched.items().getFirst().title()).isEqualTo("Nhà Giả Kim");
        assertThat(enriched.items().getFirst().imageUrl()).endsWith("5.jpg");
        assertThat(enriched.items().getFirst().quantity()).isEqualTo(2);
    }

    @Test
    void readsBooksOnceForTheWholePage() {
        List<OrderItem> first = List.of(item(5L, 1));
        List<OrderItem> second = List.of(item(6L, 1));
        List<Book> found = List.of(book(5L, "A"), book(6L, "B"));
        when(orderItems.findByOrderId(1L)).thenReturn(first);
        when(orderItems.findByOrderId(2L)).thenReturn(second);
        when(books.findByIds(anyList())).thenReturn(found);

        lookup.enrich(List.of(order(1L), order(2L)));

        verify(books, times(1)).findByIds(anyList());
        ArgumentCaptor<List<Long>> asked = ArgumentCaptor.captor();
        verify(books).findByIds(asked.capture());
        assertThat(asked.getValue()).containsExactlyInAnyOrder(5L, 6L);
    }

    @Test
    void showsOnlyTheFirstFewItems() {
        // A row listing twenty books is no more recognisable than one listing three.
        List<OrderItem> many = List.of(item(1L, 1), item(2L, 1), item(3L, 1), item(4L, 1), item(5L, 1));
        when(orderItems.findByOrderId(1L)).thenReturn(many);
        when(books.findByIds(anyList())).thenReturn(List.of());

        assertThat(lookup.enrich(List.of(order(1L))).getFirst().items()).hasSize(3);
    }

    @Test
    void keepsTheOrderWhenABookIsGone() {
        List<OrderItem> items = List.of(item(5L, 1));
        when(orderItems.findByOrderId(1L)).thenReturn(items);
        when(books.findByIds(anyList())).thenReturn(List.of());

        OrderResponse enriched = lookup.enrich(List.of(order(1L))).getFirst();

        assertThat(enriched.items()).hasSize(1);
        assertThat(enriched.items().getFirst().title()).isNull();
    }

    @Test
    void touchesNothingForAnEmptyPage() {
        assertThat(lookup.enrich(List.of())).isEmpty();
        verify(orderItems, never()).findByOrderId(anyLong());
        verifyNoInteractions(books);
    }
}
