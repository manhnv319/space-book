package com.velstrong.bookstore.infrastructure.adapter.in.rest.rental;

import com.velstrong.bookstore.application.command.rental.ForceReturnRentalCommand;
import com.velstrong.bookstore.application.command.rental.ReturnRentalCommand;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentService;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.port.in.rental.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final GetMyRentalsUseCase getMyRentalsUseCase;
    private final GetAllRentalsUseCase getAllRentalsUseCase;
    private final GetRentalUseCase getRentalUseCase;
    private final ReturnRentalUseCase returnRentalUseCase;
    private final ForceReturnRentalUseCase forceReturnRentalUseCase;
    private final RentalFulfillmentService rentalFulfillmentService;
    private final GetOverdueRentalsUseCase getOverdueRentalsUseCase;

    public RentalController(GetMyRentalsUseCase getMyRentalsUseCase, GetAllRentalsUseCase getAllRentalsUseCase,
                            GetRentalUseCase getRentalUseCase, ReturnRentalUseCase returnRentalUseCase,
                            ForceReturnRentalUseCase forceReturnRentalUseCase,
                            RentalFulfillmentService rentalFulfillmentService,
                            GetOverdueRentalsUseCase getOverdueRentalsUseCase) {
        this.getMyRentalsUseCase = getMyRentalsUseCase;
        this.getAllRentalsUseCase = getAllRentalsUseCase;
        this.getRentalUseCase = getRentalUseCase;
        this.returnRentalUseCase = returnRentalUseCase;
        this.forceReturnRentalUseCase = forceReturnRentalUseCase;
        this.rentalFulfillmentService = rentalFulfillmentService;
        this.getOverdueRentalsUseCase = getOverdueRentalsUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PagedResponse<RentalResponse>>> getMyRentals(
            @RequestAttribute Long currentUserId, @RequestParam(required = false) RentalStatus status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(getMyRentalsUseCase.getMyRentals(currentUserId, status, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> getById(@PathVariable Long id,
                                                                 @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getRentalUseCase.getById(id, currentUserId)));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<RentalResponse>> returnBook(@PathVariable Long id,
                                                                    @RequestAttribute Long currentUserId,
                                                                    @Valid @RequestBody ReturnRentalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(returnRentalUseCase.returnBook(
                new ReturnRentalCommand(id, currentUserId, request.damageFeeAmount(), request.notes()))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<RentalResponse>>> getAll(
            @RequestParam(required = false) RentalStatus status, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(getAllRentalsUseCase.getAll(status, page, size)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<PagedResponse<RentalResponse>>> getOverdue(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(getOverdueRentalsUseCase.getOverdue(page, size)));
    }

    @PostMapping("/{id}/force-return")
    public ResponseEntity<ApiResponse<RentalResponse>> forceReturn(@PathVariable Long id,
                                                                     @Valid @RequestBody ForceReturnRequest request) {
        return ResponseEntity.ok(ApiResponse.success(forceReturnRentalUseCase.forceReturn(
                new ForceReturnRentalCommand(id, request.damageFeeAmount(), request.notes()))));
    }

    @PostMapping("/start/{orderId}")
    public ResponseEntity<ApiResponse<List<RentalResponse>>> startFromOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(rentalFulfillmentService.fulfillPaidOrder(orderId)));
    }
}
