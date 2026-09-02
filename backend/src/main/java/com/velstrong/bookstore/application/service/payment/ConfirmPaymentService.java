package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.ConfirmPaymentCommand;
import com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRecordService;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRetryService;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.port.in.payment.ConfirmPaymentUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.VNPayPort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final VNPayPort vnPayPort;
    private final CommitVoucherUseCase commitVoucherUseCase;
    private final RentalFulfillmentRecordService recordService;
    private final RentalFulfillmentRetryService retryService;
    private final NotificationUseCase notifications;

    public ConfirmPaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                                 VNPayPort vnPayPort, CommitVoucherUseCase commitVoucherUseCase,
                                 RentalFulfillmentRecordService recordService,
                                 RentalFulfillmentRetryService retryService) {
        this(paymentRepository, orderRepository, vnPayPort, commitVoucherUseCase, recordService, retryService, null);
    }

    @Autowired
    public ConfirmPaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                                 VNPayPort vnPayPort, CommitVoucherUseCase commitVoucherUseCase,
                                 RentalFulfillmentRecordService recordService,
                                 RentalFulfillmentRetryService retryService, NotificationUseCase notifications) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.vnPayPort = vnPayPort;
        this.commitVoucherUseCase = commitVoucherUseCase;
        this.recordService = recordService;
        this.retryService = retryService;
        this.notifications = notifications;
    }

    @Override
    public PaymentResponse confirm(ConfirmPaymentCommand command) {
        if (!vnPayPort.verifyIpnSignature(command.vnpayParams())) {
            throw new InvalidOperationException("Invalid VNPay signature");
        }

        String orderCode = command.vnpayParams().get("vnp_TxnRef");
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderCode));
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new EntityNotFoundException("Payment for order", order.getId()));
        if (payment.isSuccess()) return PaymentResponse.from(payment);

        if (!isSuccessfulVNPayTransaction(command)) {
            payment.markFailed();
            return PaymentResponse.from(paymentRepository.save(payment));
        }

        payment.markSuccess(command.vnpayParams().get("vnp_TransactionNo"),
                command.vnpayParams().get("vnp_BankCode"));
        paymentRepository.save(payment);
        order.markPaid();
        orderRepository.save(order);
        commitVoucherUseCase.commit(new CommitVoucherCommand(order.getId()));
        scheduleRentalFulfillment(order);
        if (notifications != null) notifications.notify(order.getUserId(), NotificationType.PAYMENT, "Thanh toán thành công",
                "Đơn " + order.getOrderCode() + " đã được xác nhận thanh toán.", "/don-hang/" + order.getId());
        return PaymentResponse.from(payment);
    }

    private void scheduleRentalFulfillment(Order order) {
        if (!order.isRentalOrder() && !order.isMixedOrder()) return;
        recordService.ensurePending(order.getId());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                retryService.process(order.getId());
            }
        });
    }

    private boolean isSuccessfulVNPayTransaction(ConfirmPaymentCommand command) {
        return "00".equals(command.vnpayParams().get("vnp_ResponseCode"))
                && "00".equals(command.vnpayParams().get("vnp_TransactionStatus"));
    }
}
