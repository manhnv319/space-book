package com.velstrong.bookstore.application.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.port.in.order.GetMyOrdersUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;

@Service
@Transactional(readOnly = true)
public class GetMyOrdersService implements GetMyOrdersUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemPreviewLookup previews;

    public GetMyOrdersService(OrderRepository orderRepository, OrderItemPreviewLookup previews) {
        this.orderRepository = orderRepository;
        this.previews = previews;
    }

    @Override
    public PagedResponse<OrderResponse> getMyOrders(Long userId, OrderStatus status,
                                                     PaymentStatus paymentStatus, int page, int size) {
        var result = orderRepository.findByUserId(userId, status, paymentStatus, page, size);
        return PagedResponse.of(previews.enrich(result.content()), page, size, result.totalElements());
    }

    @Override
    public PagedResponse<OrderResponse> getMyOrdersByStatuses(Long userId,
            java.util.List<OrderStatus> statuses, int page, int size) {
        var result = orderRepository.findByUserIdAndStatuses(userId, statuses, page, size);
        return PagedResponse.of(previews.enrich(result.content()), page, size, result.totalElements());
    }

    @Override
    public java.util.Map<OrderStatus, Long> countMyOrdersByStatus(Long userId) {
        return orderRepository.countByStatusForUser(userId);
    }
}
