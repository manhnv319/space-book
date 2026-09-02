package com.velstrong.bookstore.infrastructure.adapter.in.rest.payment;

import com.velstrong.bookstore.application.command.payment.ConfirmPaymentCommand;
import com.velstrong.bookstore.application.command.payment.CreatePaymentCommand;
import com.velstrong.bookstore.application.command.payment.RefundPaymentCommand;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.application.response.payment.PaymentResponse;
import com.velstrong.bookstore.domain.port.in.payment.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final CreatePaymentUseCase createPayment;
    private final ConfirmPaymentUseCase confirmPayment;
    private final RefundPaymentUseCase refundPayment;
    private final GetPaymentHistoryUseCase history;
    private final CreateBankTransferPaymentUseCase bankTransfers;
    private final GetBankTransferQrUseCase bankTransferQr;

    public PaymentController(CreatePaymentUseCase createPayment, ConfirmPaymentUseCase confirmPayment,
                             RefundPaymentUseCase refundPayment, GetPaymentHistoryUseCase history,
                             CreateBankTransferPaymentUseCase bankTransfers, GetBankTransferQrUseCase bankTransferQr) {
        this.createPayment = createPayment; this.confirmPayment = confirmPayment;
        this.refundPayment = refundPayment; this.history = history; this.bankTransfers = bankTransfers;
        this.bankTransferQr = bankTransferQr;
    }

    @PostMapping("/vnpay/create")
    public ResponseEntity<ApiResponse<String>> createVNPayPayment(@RequestAttribute Long currentUserId,
            @Valid @RequestBody CreateVNPayRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(createPayment.createPaymentUrl(
                new CreatePaymentCommand(request.orderId(), currentUserId, httpRequest.getRemoteAddr()))));
    }

    @PostMapping("/bank-transfer/create")
    public ResponseEntity<ApiResponse<BankTransferPaymentResponse>> createBankTransfer(
            @RequestAttribute Long currentUserId, @Valid @RequestBody CreateVNPayRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bankTransfers.create(request.orderId(), currentUserId)));
    }

    @GetMapping("/bank-transfer/{orderId}")
    public ResponseEntity<ApiResponse<BankTransferPaymentResponse>> bankTransferStatus(
            @RequestAttribute Long currentUserId, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(bankTransfers.create(orderId, currentUserId)));
    }

    /**
     * Returns the QR as an image rather than a payload string so the storefront
     * can render it with a plain <img> tag. Owner-scoped through the use case.
     */
    @GetMapping(value = "/bank-transfer/{orderId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> bankTransferQr(@RequestAttribute Long currentUserId, @PathVariable Long orderId,
                                                 @RequestParam(defaultValue = "320") int size) {
        return ResponseEntity.ok()
                // A payment QR is per-order and expires; never let a proxy keep it.
                .cacheControl(CacheControl.noStore())
                .body(bankTransferQr.renderQr(orderId, currentUserId, size));
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<ApiResponse<PaymentResponse>> vnpayCallback(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(confirmPayment.confirm(new ConfirmPaymentCommand(params(request)))));
    }

    @PostMapping("/vnpay/ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        confirmPayment.confirm(new ConfirmPaymentCommand(params(request)));
        return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable Long orderId,
            @RequestAttribute Long currentUserId, @RequestBody RefundRequest request) {
        return ResponseEntity.ok(ApiResponse.success(refundPayment.refund(new RefundPaymentCommand(orderId, currentUserId, request.reason()))));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getHistory(@RequestAttribute Long currentUserId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(history.getByUserId(currentUserId, page, size)));
    }

    private Map<String, String> params(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }
}
