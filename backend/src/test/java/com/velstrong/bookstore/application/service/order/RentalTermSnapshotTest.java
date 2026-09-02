package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.ReserveVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RentalTermSnapshotTest {

    @Test
    void snapshotsSelectedRentalTermOnOrderItem() {
        OrderRepository orders = mock(OrderRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        UserRepository users = mock(UserRepository.class);
        BookRepository books = mock(BookRepository.class);
        when(users.findById(1L)).thenReturn(Optional.of(User.reconstitute(1L, "u", "h", "u@x", null, null,
                null, null, null, UserStatus.ACTIVE, List.of(), List.of())));
        when(books.findById(2L)).thenReturn(Optional.of(Book.reconstitute(2L, "isbn", "Book", null, null,
                FormatType.PAPERBACK, 100L, 10L, 50L, 100L, 20L, null, null, null, null, true, List.of(), List.of(),
                null, false, false)));
        when(orders.existsByOrderCode(any())).thenReturn(false);
        when(orders.save(any(Order.class))).thenAnswer(call -> {
            Order order = call.getArgument(0);
            return Order.reconstitute(3L, order.getUserId(), order.getOrderCode(), order.getOrderType(),
                    order.getStatus(), order.getPaymentStatus(), order.getPaymentMethod(), order.getTotalItems(),
                    order.getTotalAmount(), order.getTotalDeposit(), order.getTotalDiscount(), order.getVoucherId(),
                    order.getShippingAddressId(), order.getNotes(), order.getCreatedAt(), order.getModifiedAt(), order.getItems());
        });
        when(items.saveAll(any())).thenAnswer(call -> call.getArgument(0));
        CreateOrderService service = new CreateOrderService(orders, items, mock(PaymentRepository.class), users, books,
                mock(com.velstrong.bookstore.domain.port.out.CartRepository.class),
                mock(com.velstrong.bookstore.domain.port.out.CartItemRepository.class),
                mock(QuoteVoucherUseCase.class), mock(ReserveVoucherUseCase.class));

        service.create(new CreateOrderCommand(1L, List.of(new CreateOrderCommand.Item(2L, null, ItemType.RENTAL,
                1, 3, "DAY")), PaymentMethod.VNPAY, 1L, null, null));

        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(items).saveAll(captor.capture());
        OrderItem item = captor.getValue().getFirst();
        assertThat(item.getRentalTermValue()).isEqualTo(3);
        assertThat(item.getRentalTermUnit()).isEqualTo(RentalTermUnit.DAY);
    }
}
