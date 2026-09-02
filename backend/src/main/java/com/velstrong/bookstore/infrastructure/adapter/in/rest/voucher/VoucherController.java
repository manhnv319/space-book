package com.velstrong.bookstore.infrastructure.adapter.in.rest.voucher;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;
import com.velstrong.bookstore.domain.port.in.voucher.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

    private final CreateVoucherUseCase createVoucherUseCase;
    private final UpdateVoucherUseCase updateVoucherUseCase;
    private final DeleteVoucherUseCase deleteVoucherUseCase;
    private final GetVoucherUseCase getVoucherUseCase;
    private final GetAllVouchersUseCase getAllVouchersUseCase;
    private final QuoteVoucherUseCase quoteVoucherUseCase;

    public VoucherController(CreateVoucherUseCase createVoucherUseCase,
                              UpdateVoucherUseCase updateVoucherUseCase,
                              DeleteVoucherUseCase deleteVoucherUseCase,
                              GetVoucherUseCase getVoucherUseCase,
                              GetAllVouchersUseCase getAllVouchersUseCase,
                              QuoteVoucherUseCase quoteVoucherUseCase) {
        this.createVoucherUseCase = createVoucherUseCase;
        this.updateVoucherUseCase = updateVoucherUseCase;
        this.deleteVoucherUseCase = deleteVoucherUseCase;
        this.getVoucherUseCase = getVoucherUseCase;
        this.getAllVouchersUseCase = getAllVouchersUseCase;
        this.quoteVoucherUseCase = quoteVoucherUseCase;
    }

    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<VoucherQuoteResponse>> quote(
            @RequestAttribute Long currentUserId,
            @Valid @RequestBody QuoteVoucherRequest request) {
        QuoteVoucherCommand command = new QuoteVoucherCommand(currentUserId,
                request.voucherCode(), request.baseAmount());
        return ResponseEntity.ok(ApiResponse.success(quoteVoucherUseCase.quote(command)));
    }

    // ─── Management endpoints (F14) ──────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VoucherResponse>>> getAll(
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getAllVouchersUseCase.getAll(status, discountType, search, fromDate, toDate, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(getVoucherUseCase.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@Valid @RequestBody VoucherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createVoucherUseCase.create(request.toCommand())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody VoucherRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateVoucherUseCase.update(id, request.toCommand())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteVoucherUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Voucher deleted", null));
    }
}
