package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.rental.RentalFulfillmentStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RentalFulfillmentTest {

    @Test
    void retainsFailureForRetryThenCompletes() {
        RentalFulfillment fulfillment = RentalFulfillment.pending(1L);

        fulfillment.beginAttempt();
        fulfillment.fail("No available copy");
        assertThat(fulfillment.getStatus()).isEqualTo(RentalFulfillmentStatus.FAILED);
        assertThat(fulfillment.getAttempts()).isEqualTo(1);
        assertThat(fulfillment.canRetry()).isTrue();

        fulfillment.beginAttempt();
        fulfillment.complete();
        assertThat(fulfillment.getStatus()).isEqualTo(RentalFulfillmentStatus.COMPLETED);
        assertThat(fulfillment.getAttempts()).isEqualTo(2);
        assertThat(fulfillment.canRetry()).isFalse();
    }
}
