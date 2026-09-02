package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.command.rental.ReturnRentalCommand;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InsufficientStockException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RentalServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private BookCopyRepository bookCopyRepository;
    private RentalRepository rentalRepository;
    private StartRentalService startService;
    private ReturnRentalService returnService;
    private GetRentalService getRentalService;

    @BeforeEach
    void setUp() {
        orderRepository = org.mockito.Mockito.mock(OrderRepository.class);
        orderItemRepository = org.mockito.Mockito.mock(OrderItemRepository.class);
        bookCopyRepository = org.mockito.Mockito.mock(BookCopyRepository.class);
        rentalRepository = org.mockito.Mockito.mock(RentalRepository.class);
        startService = new StartRentalService(orderRepository, orderItemRepository, bookCopyRepository, rentalRepository);
        Clock clock = Clock.fixed(Instant.parse("2026-06-19T00:00:00Z"), ZoneId.systemDefault());
        returnService = new ReturnRentalService(rentalRepository, bookCopyRepository, clock);
        // Lookup thật với repository giả: phiếu thuê không có bản sao nào khớp
        // thì tên sách rỗng, đúng hành vi cần cho các test dưới đây.
        RentalBookLookup bookLookup = new RentalBookLookup(bookCopyRepository,
                org.mockito.Mockito.mock(com.velstrong.bookstore.domain.port.out.BookRepository.class));
        getRentalService = new GetRentalService(rentalRepository, bookLookup);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void startFromOrderRejectsMissingOrder() {
        when(orderRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> startService.startFromOrder(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void startFromOrderAssignsAvailableCopyAndDerivesTermFromOrderItem() {
        OrderItem item = rentalItem(12L, 2, RentalTermUnit.WEEK);
        BookCopy copy = BookCopy.reconstitute(100L, 10L, BookCopyStatus.AVAILABLE, BookCopyCondition.NEW, null);
        when(orderRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(baseOrder(99L, 7L)));
        when(orderItemRepository.findByOrderId(99L)).thenReturn(List.of(item));
        when(bookCopyRepository.findFirstAvailableByBookIdForUpdate(10L)).thenReturn(Optional.of(copy));

        var response = startService.startFromOrder(99L);

        assertThat(response).singleElement().satisfies(rental -> {
            assertThat(rental.bookCopyId()).isEqualTo(100L);
            assertThat(rental.rentalTermUnit()).isEqualTo(RentalTermUnit.WEEK);
            assertThat(rental.rentalTermValue()).isEqualTo(2);
            assertThat(rental.plannedReturnDate()).isEqualTo(rental.rentalStartDate().plusWeeks(2));
        });
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.RENTED);
        verify(bookCopyRepository).save(copy);
    }

    @Test
    void startFromOrderRejectsWhenNoAvailableCopyExists() {
        OrderItem item = rentalItem(12L, 1, RentalTermUnit.MONTH);
        when(orderRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(baseOrder(99L, 7L)));
        when(orderItemRepository.findByOrderId(99L)).thenReturn(List.of(item));
        when(bookCopyRepository.findFirstAvailableByBookIdForUpdate(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> startService.startFromOrder(99L))
                .isInstanceOf(InsufficientStockException.class);
        verify(bookCopyRepository, never()).save(any());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void startFromOrderSkipsAnAlreadyFulfilledItem() {
        OrderItem item = rentalItem(12L, 1, RentalTermUnit.MONTH);
        when(orderRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(baseOrder(99L, 7L)));
        when(orderItemRepository.findByOrderId(99L)).thenReturn(List.of(item));
        when(rentalRepository.existsByOrderItemId(12L)).thenReturn(true);

        assertThat(startService.startFromOrder(99L)).isEmpty();
        verify(bookCopyRepository, never()).findFirstAvailableByBookIdForUpdate(any());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void getRentalRejectsAnotherCustomersRental() {
        Rental rental = Rental.create(1L, 100L, 7L, RentalTermUnit.MONTH, 1,
                50_000L, LocalDate.now(), LocalDate.now().plusMonths(1));
        when(rentalRepository.findById(11L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> getRentalService.getById(11L, 99L))
                .isInstanceOf(BookstoreException.class);
    }

    @Test
    void returnBookRejectsForeignUser() {
        Rental rental = Rental.create(1L, 100L, 7L, RentalTermUnit.MONTH, 1,
                50_000L, LocalDate.now(), LocalDate.now().plusDays(30));
        when(rentalRepository.findById(11L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> returnService.returnBook(new ReturnRentalCommand(11L, 99L, null, null)))
                .isInstanceOf(InvalidOperationException.class);
    }

    private static OrderItem rentalItem(Long id, int value, RentalTermUnit unit) {
        return OrderItem.reconstitute(id, 99L, 10L, null, com.velstrong.bookstore.domain.model.enums.order.ItemType.RENTAL,
                1, 20_000L, 50_000L, value, unit, 20_000L);
    }

    private static Order baseOrder(Long id, Long userId) {
        return Order.reconstitute(id, userId, "ORD-1", OrderType.RENTAL,
                OrderStatus.CONFIRMED, PaymentStatus.PAID, PaymentMethod.VNPAY,
                1, 50_000L, 50_000L, 0L, null, 99L, null, LocalDateTime.now(), null, List.of());
    }
}
