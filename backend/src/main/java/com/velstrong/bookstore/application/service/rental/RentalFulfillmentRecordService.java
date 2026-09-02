package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.RentalFulfillment;
import com.velstrong.bookstore.domain.port.out.RentalFulfillmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RentalFulfillmentRecordService {

    private final RentalFulfillmentRepository repository;

    public RentalFulfillmentRecordService(RentalFulfillmentRepository repository) {
        this.repository = repository;
    }

    /** Joins payment confirmation so a paid rental always has a durable fulfillment record. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void ensurePending(Long orderId) {
        if (repository.findByOrderId(orderId).isEmpty()) repository.save(RentalFulfillment.pending(orderId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean beginAttempt(Long orderId) {
        RentalFulfillment fulfillment = get(orderId);
        if (!fulfillment.canRetry()) return false;
        fulfillment.beginAttempt();
        repository.save(fulfillment);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(Long orderId) {
        RentalFulfillment fulfillment = get(orderId);
        fulfillment.complete();
        repository.save(fulfillment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long orderId, RuntimeException exception) {
        RentalFulfillment fulfillment = get(orderId);
        fulfillment.fail(exception.getMessage());
        repository.save(fulfillment);
    }

    @Transactional(readOnly = true)
    public List<Long> retryableOrderIds(int limit) {
        return repository.findRetryable(limit).stream().map(RentalFulfillment::getOrderId).toList();
    }

    private RentalFulfillment get(Long orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("RentalFulfillment for order", orderId));
    }
}
