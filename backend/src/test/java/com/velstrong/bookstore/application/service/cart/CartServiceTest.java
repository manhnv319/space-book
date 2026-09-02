package com.velstrong.bookstore.application.service.cart;

import com.velstrong.bookstore.application.command.cart.AddCartItemCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.domain.port.out.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceTest {

    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private BookRepository bookRepository;
    private AddCartItemService addItemService;
    private GetCartService getCartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        bookRepository = mock(BookRepository.class);
        addItemService = new AddCartItemService(cartRepository, cartItemRepository, bookRepository);
        getCartService = new GetCartService(cartRepository, cartItemRepository, bookRepository);
        when(bookRepository.findById(anyLong()))
                .thenReturn(Optional.of(Book.reconstitute(10L, "isbn", "A Book", null, null,
                        FormatType.PAPERBACK, 100_000L, 5_000L, 20_000L, 60_000L, 50_000L,
                        null, null, null, null, true, List.of(), List.of(), null, false, false)));
        when(cartItemRepository.findMatching(any(), any(), any(), any(), any())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> {
            CartItem ci = inv.getArgument(0);
            return CartItem.reconstitute(ci.getId() != null ? ci.getId() : 1L, ci.getCartId(), ci.getBookId(),
                    ci.getItemType(), ci.getQuantity(), ci.getRentalTermValue(), ci.getRentalTermUnit());
        });
    }

    @Test
    @DisplayName("addItem creates a cart if user has none")
    void addItemCreatesCartWhenMissing() {
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            return Cart.reconstitute(50L, c.getUserId(), List.of());
        });
        var command = new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 2, null, null);

        var response = addItemService.addItem(command);

        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).bookId()).isEqualTo(10L);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("addItem appends to existing cart without creating a new one")
    void addItemReusesCart() {
        Cart existing = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(existing));

        var response = addItemService.addItem(new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 1, null, null));

        verify(cartRepository, never()).save(any(Cart.class));
        assertThat(response.items()).hasSize(1);
    }

    @Test
    @DisplayName("addItem with RENTAL item type persists rental term fields")
    void addItemRental() {
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            return Cart.reconstitute(50L, c.getUserId(), List.of());
        });

        var response = addItemService.addItem(
                new AddCartItemCommand(7L, 10L, ItemType.RENTAL, null, 1, "MONTH"));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).itemType()).isEqualTo(ItemType.RENTAL);
        assertThat(response.items().get(0).rentalTermValue()).isEqualTo(1);
        assertThat(response.items().get(0).rentalTermUnit()).isEqualTo("MONTH");
    }

    @Test
    @DisplayName("getCart returns an empty response without saving when no cart exists (F17)")
    void getCartDoesNotPersistWhenMissing() {
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.empty());

        var response = getCartService.getByUserId(7L);

        assertThat(response.id()).isNull();
        assertThat(response.userId()).isEqualTo(7L);
        assertThat(response.items()).isEmpty();
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository, never()).findByCartId(any());
    }

    @Test
    @DisplayName("getCart returns the existing cart with its items")
    void getCartReturnsExisting() {
        Cart cart = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        CartItem item = CartItem.reconstitute(1L, 50L, 10L, ItemType.PURCHASE, 2, null, null);
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(50L)).thenReturn(List.of(item));

        var response = getCartService.getByUserId(7L);

        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).bookId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("addItem throws 404 when book does not exist")
    void addItemThrowsWhenBookMissing() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addItemService.addItem(
                new AddCartItemCommand(7L, 999L, ItemType.PURCHASE, 1, null, null)))
                .isInstanceOf(EntityNotFoundException.class);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItem throws when book is not available for sale")
    void addItemThrowsWhenNotAvailableForSale() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(Book.reconstitute(10L, "isbn", "A Book",
                null, null, FormatType.PAPERBACK, 100_000L, 5_000L, 20_000L, 60_000L, 50_000L,
                null, null, null, null, false, List.of(), List.of(), null, false, false)));

        assertThatThrownBy(() -> addItemService.addItem(
                new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 1, null, null)))
                .isInstanceOf(InvalidOperationException.class);
        verify(cartRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("addItem throws when book has no rental price (not available for rental)")
    void addItemThrowsWhenNotAvailableForRental() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(Book.reconstitute(10L, "isbn", "A Book",
                null, null, FormatType.PAPERBACK, 100_000L, null, null, null, null,
                null, null, null, null, true, List.of(), List.of(), null, false, false)));

        assertThatThrownBy(() -> addItemService.addItem(
                new AddCartItemCommand(7L, 10L, ItemType.RENTAL, null, 1, "MONTH")))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("addItem merges duplicate PURCHASE of the same book by summing quantity")
    void addItemMergesDuplicatePurchase() {
        Cart existing = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(existing));
        CartItem matched = CartItem.reconstitute(1L, 50L, 10L, ItemType.PURCHASE, 2, null, null);
        when(cartItemRepository.findMatching(50L, 10L, ItemType.PURCHASE, null, null))
                .thenReturn(Optional.of(matched));

        var response = addItemService.addItem(new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 3, null, null));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(5);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItem caps merged PURCHASE quantity at 99")
    void addItemMergeCapsQuantity() {
        Cart existing = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(existing));
        CartItem matched = CartItem.reconstitute(1L, 50L, 10L, ItemType.PURCHASE, 95, null, null);
        when(cartItemRepository.findMatching(50L, 10L, ItemType.PURCHASE, null, null))
                .thenReturn(Optional.of(matched));

        var response = addItemService.addItem(new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 50, null, null));

        assertThat(response.items().get(0).quantity()).isEqualTo(99);
    }

    @Test
    @DisplayName("addItem keeps a single row when RENTAL is added twice with the same term")
    void addItemRentalSameTermDoesNotDuplicate() {
        Cart existing = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(existing));
        CartItem matched = CartItem.reconstitute(1L, 50L, 10L, ItemType.RENTAL, 1, 1, "MONTH");
        when(cartItemRepository.findMatching(50L, 10L, ItemType.RENTAL, 1, "MONTH"))
                .thenReturn(Optional.of(matched));

        var response = addItemService.addItem(
                new AddCartItemCommand(7L, 10L, ItemType.RENTAL, null, 1, "MONTH"));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(1);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItem creates a separate row when RENTAL term differs")
    void addItemRentalDifferentTermCreatesNewRow() {
        Cart existing = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L)).thenReturn(Optional.of(existing));
        when(cartItemRepository.findMatching(50L, 10L, ItemType.RENTAL, 1, "WEEK"))
                .thenReturn(Optional.empty());

        var response = addItemService.addItem(
                new AddCartItemCommand(7L, 10L, ItemType.RENTAL, null, 1, "WEEK"));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).rentalTermUnit()).isEqualTo("WEEK");
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItem retries cart lookup when concurrent insert violates the unique constraint (race condition)")
    void addItemRecoversFromConcurrentCartCreation() {
        Cart racedCart = Cart.reconstitute(50L, 7L, new java.util.ArrayList<>());
        when(cartRepository.findByUserId(7L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(racedCart));
        when(cartRepository.save(any(Cart.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint uk_carts_user_id"));

        var response = addItemService.addItem(new AddCartItemCommand(7L, 10L, ItemType.PURCHASE, 1, null, null));

        assertThat(response.id()).isEqualTo(50L);
        verify(cartRepository, times(2)).findByUserId(7L);
    }
}
