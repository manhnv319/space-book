package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.RentalFulfillment;

import java.util.List;
import java.util.Optional;

public interface RentalFulfillmentRepository {
    RentalFulfillment save(RentalFulfillment fulfillment);
    Optional<RentalFulfillment> findByOrderId(Long orderId);
    List<RentalFulfillment> findRetryable(int limit);
}
