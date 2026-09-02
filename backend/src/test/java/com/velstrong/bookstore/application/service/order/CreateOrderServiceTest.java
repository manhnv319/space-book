package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.command.voucher.ReserveVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.ReserveVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private com.velstrong.bookstore.domain.port.out.CartRepository cartRepository;
    private com.velstrong.bookstore.domain.port.out.CartItemRepository cartItemRepository;
    private QuoteVoucherUseCase quoteVoucherUseCase;
    private ReserveVoucherUseCase reserveVoucherUseCase;
    private CreateOrderService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);
        quoteVoucherUseCase = mock(QuoteVoucherUseCase.class);
        reserveVoucherUseCase = mock(ReserveVoucherUseCase.class);
        cartRepository = mock(com.velstrong.bookstore.domain.port.out.CartRepository.class);
        cartItemRepository = mock(com.velstrong.bookstore.domain.port.out.CartItemRepository.class);
        service = new CreateOrderService(orderRepository, orderItemRepository,
                paymentRepository, userRepository, bookRepository, cartRepository, cartItemRepository,
                quoteVoucherUseCase, reserveVoucherUseCase);
        // Prices are server-derived: list 100_000, rental week 20_000, deposit 50_000.
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(anyBook()));
        when(orderRepository.existsByOrderCode(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            return Order.reconstitute(99L, o.getUserId(), o.getOrderCode(), o.getOrderType(),
                    o.getStatus(), o.getPaymentStatus(), o.getPaymentMethod(),
                    o.getTotalItems(), o.getTotalAmount(), o.getTotalDeposit(), o.getTotalDiscount(),
                    o.getVoucherId(), o.getShippingAddressId(), o.getNotes(),
                    o.getCreatedAt(), o.getModifiedAt(), o.getItems());
        });
        when(orderItemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("creates a PURCHASE order for an existing user with valid items")
    void createsPurchaseOrder() {
        User user = anyUser();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 2, null, null)),
                PaymentMethod.VNPAY, 99L, null, "note");

        var response = service.create(command);

        assertThat(response.orderCode()).startsWith("ORD-");
        assertThat(response.orderType()).isEqualTo(OrderType.PURCHASE);
        assertThat(response.totalItems()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualTo(200_000L);
        verify(paymentRepository).save(any());
        verify(reserveVoucherUseCase, never()).reserve(any());
    }

    @Test
    @DisplayName("rejects unknown user")
    void rejectsUnknownUser() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, 99L, null, null);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("rejects empty items list")
    void rejectsEmptyItems() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        var command = new CreateOrderCommand(7L, List.of(), PaymentMethod.VNPAY, 99L, null, null);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    @DisplayName("rejects null payment method")
    void rejectsNullPaymentMethod() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                null, 99L, null, null);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Payment method");
    }

    @Test
    @DisplayName("rejects missing shipping address")
    void rejectsMissingShipping() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, null, null, null);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Shipping address");
    }

    @Test
    @DisplayName("applies voucher discount when quote is valid and reserves it")
    void appliesVoucherAndReserves() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        when(quoteVoucherUseCase.quote(any(QuoteVoucherCommand.class)))
                .thenReturn(VoucherQuoteResponse.valid(20_000L, 80_000L));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, 99L, "SALE20", null);

        service.create(command);

        ArgumentCaptor<ReserveVoucherCommand> captor = ArgumentCaptor.forClass(ReserveVoucherCommand.class);
        verify(reserveVoucherUseCase).reserve(captor.capture());
        assertThat(captor.getValue().voucherCode()).isEqualTo("SALE20");
        assertThat(captor.getValue().orderId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("does not reserve voucher when quote is invalid (e.g. expired)")
    void doesNotReserveWhenQuoteInvalid() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        when(quoteVoucherUseCase.quote(any(QuoteVoucherCommand.class)))
                .thenReturn(VoucherQuoteResponse.invalid(
                        com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason.EXPIRED));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, 99L, "SALE20", null);

        service.create(command);

        verify(reserveVoucherUseCase, never()).reserve(any());
    }

    @Test
    @DisplayName("order type is MIXED when items include both purchase and rental")
    void mixedOrderType() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        var command = new CreateOrderCommand(7L, List.of(
                new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null),
                new CreateOrderCommand.Item(11L, 100L, ItemType.RENTAL, 1, null, "WEEK")
        ), PaymentMethod.VNPAY, 99L, null, null);

        var response = service.create(command);
        assertThat(response.orderType()).isEqualTo(OrderType.MIXED);
    }

    @Test
    @DisplayName("payment is created with the order's final amount")
    void paymentCreatedWithFinalAmount() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        when(quoteVoucherUseCase.quote(any(QuoteVoucherCommand.class)))
                .thenReturn(VoucherQuoteResponse.valid(10_000L, 90_000L));
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, 99L, "SALE10", null);

        service.create(command);

        ArgumentCaptor<com.velstrong.bookstore.domain.model.Payment> captor =
                ArgumentCaptor.forClass(com.velstrong.bookstore.domain.model.Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(90_000L);
    }

    @Test
    @DisplayName("regenerates order code when collision detected")
    void regeneratesOrderCodeOnCollision() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(anyUser()));
        when(orderRepository.existsByOrderCode(any()))
                .thenReturn(true)   // first call collides
                .thenReturn(false); // second call OK
        var command = new CreateOrderCommand(7L,
                List.of(new CreateOrderCommand.Item(10L, null, ItemType.PURCHASE, 1, null, null)),
                PaymentMethod.VNPAY, 99L, null, null);

        var response = service.create(command);
        assertThat(response.orderCode()).startsWith("ORD-");
    }

    private static User anyUser() {
        return User.reconstitute(7L, "u", "hash", "u@x",
                null, null, null, null, null,
                UserStatus.ACTIVE, List.of(), List.of());
    }

    private static Book anyBook() {
        return Book.reconstitute(10L, "isbn", "A Book", null, null, FormatType.PAPERBACK,
                100_000L, 5_000L, 20_000L, 60_000L, 50_000L,
                null, null, null, null, true, List.of(), List.of(), null, false, false);
    }
}
