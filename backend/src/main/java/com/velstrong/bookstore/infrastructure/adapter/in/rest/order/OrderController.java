package com.velstrong.bookstore.infrastructure.adapter.in.rest.order;

import com.velstrong.bookstore.application.command.order.CancelOrderCommand;
import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.order.OrderDetailResponse;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.in.order.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetMyOrdersUseCase getMyOrdersUseCase;
    private final GetAllOrdersUseCase getAllOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, CancelOrderUseCase cancelOrderUseCase,
                           GetOrderUseCase getOrderUseCase, GetMyOrdersUseCase getMyOrdersUseCase,
                           GetAllOrdersUseCase getAllOrdersUseCase,
                           UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.getMyOrdersUseCase = getMyOrdersUseCase;
        this.getAllOrdersUseCase = getAllOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest request,
                                                              @RequestAttribute Long currentUserId) {
        CreateOrderCommand command = request.toCommand(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createOrderUseCase.create(command)));
    }

    /**
     * Danh sách đơn của khách, lọc theo nhiều trạng thái.
     *
     * Nhận `statuses` lặp lại được (`?statuses=CONFIRMED&statuses=PROCESSING`) vì
     * một tab ở giao diện gộp nhiều trạng thái nội bộ; lọc từng cái một thì số
     * trang trả về sẽ sai.
     */
    @GetMapping("/me/by-status")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getMyOrdersByStatuses(
            @RequestAttribute Long currentUserId,
            @RequestParam(required = false) java.util.List<OrderStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getMyOrdersUseCase.getMyOrdersByStatuses(currentUserId, statuses, page, size)));
    }

    /** Số đơn theo trạng thái, để giao diện hiển thị số đếm trên từng tab. */
    @GetMapping("/me/summary")
    public ResponseEntity<ApiResponse<java.util.Map<OrderStatus, Long>>> myOrderSummary(
            @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getMyOrdersUseCase.countMyOrdersByStatus(currentUserId)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getMyOrders(
            @RequestAttribute Long currentUserId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getMyOrdersUseCase.getMyOrders(currentUserId, status, paymentStatus, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getById(@PathVariable Long id,
                                                                    @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getOrderUseCase.getById(id, currentUserId)));
    }

    /** Staff-only detail: no customer ownership check; access is policy-gated. */
    @GetMapping("/{id}/management")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getForManagement(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(getOrderUseCase.getForManagement(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long id,
                                                              @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(
                cancelOrderUseCase.cancel(new CancelOrderCommand(id, currentUserId))));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id,
                                                                    @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateOrderStatusUseCase.updateStatus(request.toCommand(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getAllOrdersUseCase.getAll(status, paymentStatus, page, size, fromDate, toDate, search)));
    }
}
