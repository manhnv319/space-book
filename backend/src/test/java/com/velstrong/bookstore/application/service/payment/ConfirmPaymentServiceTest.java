package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.ConfirmPaymentCommand;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRecordService;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRetryService;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.*;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.VNPayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfirmPaymentServiceTest {

    private PaymentRepository payments;
    private OrderRepository orders;
    private VNPayPort vnPay;
    private RentalFulfillmentRecordService records;
    private RentalFulfillmentRetryService retries;
    private ConfirmPaymentService service;

    @BeforeEach
    void setUp() {
        payments = mock(PaymentRepository.class);
        orders = mock(OrderRepository.class);
        vnPay = mock(VNPayPort.class);
        records = mock(RentalFulfillmentRecordService.class);
        retries = mock(RentalFulfillmentRetryService.class);
        service = new ConfirmPaymentService(payments, orders, vnPay, mock(CommitVoucherUseCase.class), records, retries);
    }

    @Test
    void persistsPendingRecordThenProcessesOnlyAfterCommit() {
        Order order = order(OrderType.RENTAL, PaymentStatus.UNPAID);
        Payment payment = Payment.create(1L, 100_000L, PaymentMethod.VNPAY);
        when(vnPay.verifyIpnSignature(successParams())).thenReturn(true);
        when(orders.findByOrderCode("ORD-1")).thenReturn(Optional.of(order));
        when(payments.findByOrderId(1L)).thenReturn(Optional.of(payment));

        TransactionSynchronizationManager.initSynchronization();
        try {
            assertEquals(PaymentTransactionStatus.SUCCESS, service.confirm(new ConfirmPaymentCommand(successParams())).status());
            assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
            verify(payments).save(payment);
            verify(orders).save(order);
            verify(records).ensurePending(1L);
            verify(retries, never()).process(1L);
            TransactionSynchronizationManager.getSynchronizations().forEach(sync -> sync.afterCommit());
            verify(retries).process(1L);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void duplicateSuccessfulCallbackDoesNotCreateOrProcessAnotherFulfillment() {
        Payment payment = Payment.reconstitute(5L, 1L, 100_000L, PaymentMethod.VNPAY,
                PaymentTransactionStatus.SUCCESS, "TXN-OLD", "NCB", LocalDateTime.now(), LocalDateTime.now());
        when(vnPay.verifyIpnSignature(successParams())).thenReturn(true);
        when(orders.findByOrderCode("ORD-1")).thenReturn(Optional.of(order(OrderType.RENTAL, PaymentStatus.PAID)));
        when(payments.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertEquals(PaymentTransactionStatus.SUCCESS, service.confirm(new ConfirmPaymentCommand(successParams())).status());
        verify(records, never()).ensurePending(any());
        verify(retries, never()).process(any());
    }

    private static Map<String, String> successParams() {
        return Map.of("vnp_TxnRef", "ORD-1", "vnp_ResponseCode", "00", "vnp_TransactionStatus", "00",
                "vnp_TransactionNo", "TXN-99", "vnp_BankCode", "NCB");
    }

    private static Order order(OrderType type, PaymentStatus status) {
        return Order.reconstitute(1L, 10L, "ORD-1", type, OrderStatus.CONFIRMED, status, PaymentMethod.VNPAY,
                1, 100_000L, 0L, 0L, null, 99L, null, LocalDateTime.now(), null, List.of());
    }
}
