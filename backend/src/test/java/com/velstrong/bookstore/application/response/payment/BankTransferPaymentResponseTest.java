package com.velstrong.bookstore.application.response.payment;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The payment deadline crosses a timezone boundary: it is stored naively in the
 * server's zone and read by a browser in the customer's. Sent without an offset
 * the browser reads it as its own local time, so a payment created in UTC and
 * opened in +07 looked seven hours expired the moment it was created.
 */
class BankTransferPaymentResponseTest {

    @Test
    void convertsTheStoredDeadlineToAnAbsolutePointInTime() {
        LocalDateTime stored = LocalDateTime.of(2026, 7, 26, 19, 0);

        Instant converted = BankTransferPaymentResponse.toInstant(stored);

        assertThat(converted).isEqualTo(stored.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void aDeadlineThirtyMinutesOutIsStillInTheFutureWhateverTheServerZoneIs() {
        // The bug in one line: the naive value alone cannot answer this.
        LocalDateTime stored = LocalDateTime.now().plusMinutes(30);

        assertThat(BankTransferPaymentResponse.toInstant(stored)).isAfter(Instant.now());
    }

    @Test
    void passesNullThrough() {
        assertThat(BankTransferPaymentResponse.toInstant(null)).isNull();
    }
}
