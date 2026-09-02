package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Reproduces the failure that broke the bank-transfer screen in production.
 *
 * The versioned entities were persisted by building a fresh instance and setting
 * its id. That is a detached entity with a null optimistic-lock value, so
 * Hibernate rejected it with DataIntegrityViolationException. Only the insert
 * path (null id) worked, which is why the bug stayed hidden until something
 * updated a row — assigning a transfer reference, marking an order paid,
 * cancelling, renting a copy out.
 *
 * Insert-then-update is therefore the case worth pinning, not insert alone.
 */
@SpringBootTest
@Transactional
class VersionedEntityUpdateTest {

    @Autowired
    private PaymentRepository payments;

    @Autowired
    private OrderRepository orders;

    @Test
    void updatingAnExistingPaymentDoesNotFailOnTheOptimisticLockColumn() {
        Payment inserted = payments.save(
                Payment.create(999_999_001L, 250_000L, PaymentMethod.BANK_TRANSFER));
        assertThat(inserted.getId()).as("insert path has always worked").isNotNull();

        inserted.initializeBankTransfer("TQTEST12345", LocalDateTime.now().plusMinutes(30));

        assertThatCode(() -> payments.save(inserted)).doesNotThrowAnyException();

        assertThat(payments.findById(inserted.getId()).orElseThrow().getTransferReference())
                .isEqualTo("TQTEST12345");
    }

    @Test
    void aSecondUpdateStillWorksOnceTheVersionHasBeenBumped() {
        Payment payment = payments.save(
                Payment.create(999_999_002L, 100_000L, PaymentMethod.BANK_TRANSFER));
        payment.initializeBankTransfer("TQTEST54321", LocalDateTime.now().plusMinutes(30));
        Payment stored = payments.save(payment);

        stored.markSuccess("txn-1", "TIMO_IMAP");

        assertThatCode(() -> payments.save(stored)).doesNotThrowAnyException();
        assertThat(payments.findById(stored.getId()).orElseThrow().isSuccess()).isTrue();
    }

    @Test
    void ordersCanBeUpdatedToo() {
        // Confirming a transfer saves the order right after the payment, so an
        // order that cannot be updated would break the flow just as badly.
        orders.findById(1L).ifPresent(order ->
                assertThatCode(() -> orders.save(order)).doesNotThrowAnyException());
    }
}
