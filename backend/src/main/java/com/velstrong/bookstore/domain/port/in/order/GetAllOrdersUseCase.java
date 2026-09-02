package com.velstrong.bookstore.domain.port.in.order;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;

import java.time.LocalDate;

public interface GetAllOrdersUseCase {
    PagedResponse<OrderResponse> getAll(OrderStatus status, PaymentStatus paymentStatus,
                                        int page, int size, LocalDate fromDate, LocalDate toDate, String search);
}
