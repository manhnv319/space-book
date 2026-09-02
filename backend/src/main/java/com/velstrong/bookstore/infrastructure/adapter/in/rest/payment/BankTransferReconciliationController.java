package com.velstrong.bookstore.infrastructure.adapter.in.rest.payment;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.UnmatchedTransferResponse;
import com.velstrong.bookstore.application.command.payment.ResolveUnmatchedTransferCommand;
import com.velstrong.bookstore.domain.port.in.payment.GetUnmatchedTransfersUseCase;
import com.velstrong.bookstore.domain.port.in.payment.ResolveUnmatchedTransferUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Staff view over credits that could not be attached to an order.
 *
 * Guarded by {@code payment:refund} in security-endpoints.yml — the permission
 * already held by the roles that handle money, so no new grant has to be seeded.
 */
@RestController
@RequestMapping("/api/v1/bank-transfers")
public class BankTransferReconciliationController {

    private final GetUnmatchedTransfersUseCase unmatchedTransfers;
    private final ResolveUnmatchedTransferUseCase resolver;

    public BankTransferReconciliationController(GetUnmatchedTransfersUseCase unmatchedTransfers, ResolveUnmatchedTransferUseCase resolver) {
        this.unmatchedTransfers = unmatchedTransfers; this.resolver = resolver;
    }

    @GetMapping("/unmatched")
    public ResponseEntity<ApiResponse<PagedResponse<UnmatchedTransferResponse>>> unmatched(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(unmatchedTransfers.getAll(page, size)));
    }

    @PostMapping("/unmatched/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolve(@PathVariable Long id, @Valid @RequestBody ResolveRequest request) {
        resolver.resolve(new ResolveUnmatchedTransferCommand(id, request.orderId()));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    public record ResolveRequest(@NotNull Long orderId) {}
}
