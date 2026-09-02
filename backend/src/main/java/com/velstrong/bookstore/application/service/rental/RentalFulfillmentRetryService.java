package com.velstrong.bookstore.application.service.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RentalFulfillmentRetryService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalFulfillmentRetryService.class);
    private static final int BATCH_SIZE = 50;

    private final RentalFulfillmentRecordService recordService;
    private final RentalFulfillmentService fulfillmentService;

    public RentalFulfillmentRetryService(RentalFulfillmentRecordService recordService,
                                         RentalFulfillmentService fulfillmentService) {
        this.recordService = recordService;
        this.fulfillmentService = fulfillmentService;
    }

    /** Safe for post-commit optimization and scheduled retries; rental creation is idempotent per order item. */
    public void process(Long orderId) {
        if (!recordService.beginAttempt(orderId)) return;
        try {
            fulfillmentService.fulfillPaidOrder(orderId);
            recordService.markCompleted(orderId);
        } catch (RuntimeException exception) {
            recordService.markFailed(orderId, exception);
            LOG.warn("Rental fulfillment attempt failed: orderId={}", orderId, exception);
        }
    }

    @Scheduled(fixedDelayString = "${app.rental-fulfillment.retry-delay-ms:60000}")
    public void retryPending() {
        recordService.retryableOrderIds(BATCH_SIZE).forEach(this::process);
    }
}
