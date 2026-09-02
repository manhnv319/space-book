package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.ReserveVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Ordering used to leave the cart untouched. The books stayed in it, so pressing
 * "place order" again built a duplicate order for the same titles — and the
 * customer could end up paying for both.
 */
class CartClearedAfterOrderTest {

    private CartRepository carts;
    private CartItemRepository cartItems;
    private CreateOrderService service;

    private static final Long USER = 1L;
    private static final Long CART = 9L;
    private static final Long BOOK = 2L;

    @BeforeEach
    void setUp() {
        OrderRepository orders = mock(OrderRepository.class);
        OrderItemRepository orderItems = mock(OrderItemRepository.class);
        UserRepository users = mock(UserRepository.class);
        BookRepository books = mock(BookRepository.class);
        carts = mock(CartRepository.class);
        cartItems = mock(CartItemRepository.class);

        when(users.findById(USER)).thenReturn(Optional.of(
                User.reconstitute(USER, "reader", "x", "r@example.test", "Reader", null, null, null, null,
                        UserStatus.ACTIVE, List.of(), List.of())));
        when(books.findById(anyLong())).thenReturn(Optional.of(Book.reconstitute(BOOK, "isbn", "Nhà Giả Kim",
                "", null, FormatType.PAPERBACK, 100_000L, 5_000L, 20_000L, 60_000L, 50_000L,
                (short) 2020, "NXB", "vi", (short) 200, true, List.of(), List.of(), null, false, false)));
        when(orders.existsByOrderCode(anyString())).thenReturn(false);
        when(orders.save(any(Order.class))).thenAnswer(call -> {
            Order given = call.getArgument(0);
            return Order.reconstitute(77L, given.getUserId(), given.getOrderCode(), given.getOrderType(),
                    given.getStatus(), given.getPaymentStatus(), given.getPaymentMethod(), given.getTotalItems(),
                    given.getTotalAmount(), given.getTotalDeposit(), given.getTotalDiscount(), given.getVoucherId(),
                    given.getShippingAddressId(), given.getNotes(), given.getCreatedAt(), given.getModifiedAt(),
                    given.getItems());
        });
        when(orderItems.saveAll(any())).thenAnswer(call -> call.getArgument(0));
        when(carts.findByUserId(USER)).thenReturn(Optional.of(Cart.reconstitute(CART, USER, List.of())));

        service = new CreateOrderService(orders, orderItems, mock(PaymentRepository.class), users, books,
                carts, cartItems, mock(QuoteVoucherUseCase.class), mock(ReserveVoucherUseCase.class));
    }

    private void order(int quantity) {
        service.create(new CreateOrderCommand(USER,
                List.of(new CreateOrderCommand.Item(BOOK, null, ItemType.PURCHASE, quantity, null, null)),
                PaymentMethod.BANK_TRANSFER, 5L, null, null));
    }

    private CartItem lineWithQuantity(int quantity) {
        CartItem line = mock(CartItem.class);
        when(line.getId()).thenReturn(31L);
        when(line.getQuantity()).thenReturn(quantity);
        return line;
    }

    @Test
    void takesTheOrderedLineOutOfTheCart() {
        CartItem line = lineWithQuantity(1);
        when(cartItems.findMatching(eq(CART), eq(BOOK), eq(ItemType.PURCHASE), any(), any()))
                .thenReturn(Optional.of(line));

        order(1);

        verify(cartItems).deleteById(31L);
    }

    @Test
    void reducesInsteadOfRemovingWhenOnlyPartOfTheLineWasOrdered() {
        // The API accepts a partial order; wiping the line would swallow books the
        // customer still means to buy.
        CartItem line = lineWithQuantity(5);
        when(cartItems.findMatching(eq(CART), eq(BOOK), eq(ItemType.PURCHASE), any(), any()))
                .thenReturn(Optional.of(line));

        order(2);

        verify(line).updateQuantity(3);
        verify(cartItems).save(line);
        verify(cartItems, never()).deleteById(anyLong());
    }

    @Test
    void leavesOtherLinesAlone() {
        // Only what was ordered goes; clearing the whole cart would lose the rest.
        // Mock dựng trước: gọi when() lồng trong when() làm Mockito hiểu nhầm.
        CartItem line = lineWithQuantity(1);
        when(cartItems.findMatching(eq(CART), eq(BOOK), eq(ItemType.PURCHASE), any(), any()))
                .thenReturn(Optional.of(line));

        order(1);

        verify(cartItems, never()).deleteByCartId(anyLong());
    }

    @Test
    void ordersFineWhenTheBookIsNoLongerInTheCart() {
        // Reorder posts items that were never in the cart, and that must not fail.
        when(cartItems.findMatching(anyLong(), anyLong(), any(), any(), any())).thenReturn(Optional.empty());

        order(1);

        verify(cartItems, never()).deleteById(anyLong());
        assertThat(true).isTrue();
    }
}
