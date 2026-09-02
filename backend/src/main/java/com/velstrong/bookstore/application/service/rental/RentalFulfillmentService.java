package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.port.in.rental.StartRentalUseCase;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RentalFulfillmentService {

    private final OrderRepository orderRepository;
    private final StartRentalUseCase startRentalUseCase;

    public RentalFulfillmentService(OrderRepository orderRepository, StartRentalUseCase startRentalUseCase) {
        this.orderRepository = orderRepository;
        this.startRentalUseCase = startRentalUseCase;
    }

    /** Safe to invoke repeatedly after payment: StartRentalUseCase is idempotent per order item. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<RentalResponse> fulfillPaidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (!order.getPaymentStatus().isPaid()) {
            throw new InvalidOperationException("Cannot fulfill an unpaid order");
        }
        if (!order.isRentalOrder() && !order.isMixedOrder()) return List.of();
        return startRentalUseCase.startFromOrder(orderId);
    }
}
