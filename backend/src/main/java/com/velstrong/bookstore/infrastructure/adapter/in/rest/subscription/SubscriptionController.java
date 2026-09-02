package com.velstrong.bookstore.infrastructure.adapter.in.rest.subscription;

import com.velstrong.bookstore.application.command.subscription.CancelSubscriptionCommand;
import com.velstrong.bookstore.application.command.subscription.ExtendSubscriptionCommand;
import com.velstrong.bookstore.application.command.subscription.PurchaseSubscriptionCommand;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;
import com.velstrong.bookstore.application.response.subscription.SubscriptionResponse;
import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.domain.port.in.subscription.*;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final GetActiveSubscriptionsUseCase getActiveSubscriptionsUseCase;
    private final GetAllSubscriptionsUseCase getAllSubscriptionsUseCase;
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final PurchaseSubscriptionUseCase purchaseSubscriptionUseCase;
    private final GetSubscriptionPaymentUseCase subscriptionPayment;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final ExtendSubscriptionUseCase extendSubscriptionUseCase;
    private final GetMySubscriptionsUseCase getMySubscriptionsUseCase;
    private final GetActiveSubscriptionUseCase getActiveSubscriptionUseCase;

    public SubscriptionController(GetActiveSubscriptionsUseCase getActiveSubscriptionsUseCase,
                                   GetAllSubscriptionsUseCase getAllSubscriptionsUseCase,
                                   CreateSubscriptionUseCase createSubscriptionUseCase,
                                   PurchaseSubscriptionUseCase purchaseSubscriptionUseCase,
                                   CancelSubscriptionUseCase cancelSubscriptionUseCase,
                                   ExtendSubscriptionUseCase extendSubscriptionUseCase,
                                   GetMySubscriptionsUseCase getMySubscriptionsUseCase,
                                   GetActiveSubscriptionUseCase getActiveSubscriptionUseCase,
            GetSubscriptionPaymentUseCase subscriptionPayment) {
        this.getActiveSubscriptionsUseCase = getActiveSubscriptionsUseCase;
        this.getAllSubscriptionsUseCase = getAllSubscriptionsUseCase;
        this.createSubscriptionUseCase = createSubscriptionUseCase;
        this.purchaseSubscriptionUseCase = purchaseSubscriptionUseCase;
        this.subscriptionPayment = subscriptionPayment;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
        this.extendSubscriptionUseCase = extendSubscriptionUseCase;
        this.getMySubscriptionsUseCase = getMySubscriptionsUseCase;
        this.getActiveSubscriptionUseCase = getActiveSubscriptionUseCase;
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(getActiveSubscriptionsUseCase.getActive()));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> purchase(
            @RequestAttribute Long currentUserId,
            @Valid @RequestBody PurchaseSubscriptionRequest request) {
        PurchaseSubscriptionCommand command = new PurchaseSubscriptionCommand(currentUserId, request.subscriptionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(purchaseSubscriptionUseCase.purchase(command)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerSubscriptionResponse>>> getMySubscriptions(
            @RequestAttribute Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getMySubscriptionsUseCase.getMySubscriptions(currentUserId, page, size)));
    }

    @GetMapping("/me/active")
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> getMyActive(
            @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(
                getActiveSubscriptionUseCase.getActiveByUserId(currentUserId)));
    }

    @PostMapping("/me/{id}/cancel")
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> cancel(
            @RequestAttribute Long currentUserId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                cancelSubscriptionUseCase.cancel(new CancelSubscriptionCommand(id, currentUserId))));
    }

    @PostMapping("/me/{id}/extend")
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> extend(
            @RequestAttribute Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody ExtendSubscriptionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                extendSubscriptionUseCase.extend(new ExtendSubscriptionCommand(id, currentUserId, request.additionalDays()))));
    }

    // ─── Management endpoints ──────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(getAllSubscriptionsUseCase.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> create(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                createSubscriptionUseCase.create(request.name(), request.description(),
                        request.price(), request.durationDays(), request.maxRentals())));
    }

    /** Thông tin chuyển khoản cho gói đang chờ thanh toán. */
    @GetMapping("/me/{id}/payment")
    public ResponseEntity<ApiResponse<BankTransferPaymentResponse>> payment(
            @RequestAttribute Long currentUserId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionPayment.getPayment(id, currentUserId)));
    }

    @GetMapping(value = "/me/{id}/payment/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> paymentQr(@RequestAttribute Long currentUserId, @PathVariable Long id,
                                            @RequestParam(defaultValue = "320") int size) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(subscriptionPayment.renderQr(id, currentUserId, size));
    }
}
