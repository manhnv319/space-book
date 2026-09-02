package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.in.order.GetAllOrdersUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class GetAllOrdersService implements GetAllOrdersUseCase {

    private final OrderRepository orderRepository;

    public GetAllOrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PagedResponse<OrderResponse> getAll(OrderStatus status, PaymentStatus paymentStatus,
                                               int page, int size, LocalDate fromDate,
                                               LocalDate toDate, String search) {
        var result = orderRepository.findAll(status, paymentStatus, page, size, fromDate, toDate, search);
        return PagedResponse.of(
                result.content().stream().map(OrderResponse::from).toList(),
                page, size, result.totalElements());
    }
}
