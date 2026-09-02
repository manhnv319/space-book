package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.ResolveUnmatchedTransferCommand;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRecordService;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRetryService;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResolveUnmatchedTransferServiceTest {
    @Test
    void rejectsTransferWhenAmountDoesNotMatchPayment() {
        BankTransferReconciliationRepository transfers = mock(); PaymentRepository payments = mock(); OrderRepository orders = mock();
        UnmatchedTransfer transfer = new UnmatchedTransfer(7L, "X", 100L, LocalDateTime.now(), "manual", LocalDateTime.now());
        Order order = mock(); Payment payment = mock();
        when(transfers.findById(7L)).thenReturn(Optional.of(transfer)); when(orders.findByIdForUpdate(9L)).thenReturn(Optional.of(order));
        when(orders.findByIdForUpdate(9L)).thenReturn(Optional.of(order)); when(order.getId()).thenReturn(9L); when(payments.findByOrderId(9L)).thenReturn(Optional.of(payment));
        when(payment.getMethod()).thenReturn(PaymentMethod.BANK_TRANSFER); when(payment.isSuccess()).thenReturn(false); when(payment.getAmount()).thenReturn(99L);
        ResolveUnmatchedTransferService service = new ResolveUnmatchedTransferService(transfers, payments, orders, mock(OrderStatusHistoryRepository.class), mock(CommitVoucherUseCase.class), mock(RentalFulfillmentRecordService.class), mock(RentalFulfillmentRetryService.class));
        assertThatThrownBy(() -> service.resolve(new ResolveUnmatchedTransferCommand(7L, 9L))).isInstanceOf(InvalidOperationException.class);
        verify(transfers, never()).deleteById(any()); verify(payments, never()).save(any());
    }
}
