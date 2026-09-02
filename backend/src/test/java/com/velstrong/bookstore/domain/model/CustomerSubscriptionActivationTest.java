package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Buying a plan used to activate it on the spot, so a customer could rent
 * against a subscription they had not paid for. It now waits for the money.
 */
class CustomerSubscriptionActivationTest {

    @Test
    void aNewPurchaseCannotBeUsedYet() {
        CustomerSubscription pending = CustomerSubscription.createPendingPayment(1L, 2L);

        assertThat(pending.getStatus()).isEqualTo(CustomerSubscriptionStatus.PENDING_PAYMENT);
        assertThat(pending.getStatus().isActive()).isFalse();
        assertThat(pending.getStatus().isAwaitingPayment()).isTrue();
    }

    @Test
    void hasNoTermUntilItIsPaidFor() {
        // Dates set at purchase time would silently burn days while the customer
        // is still arranging the transfer.
        CustomerSubscription pending = CustomerSubscription.createPendingPayment(1L, 2L);

        assertThat(pending.getStartDate()).isNull();
        assertThat(pending.getEndDate()).isNull();
    }

    @Test
    void theTermStartsWhenTheMoneyArrives() {
        CustomerSubscription pending = CustomerSubscription.createPendingPayment(1L, 2L);
        LocalDate paidOn = LocalDate.of(2026, 7, 27);

        pending.activate(paidOn, 30);

        assertThat(pending.getStatus()).isEqualTo(CustomerSubscriptionStatus.ACTIVE);
        assertThat(pending.getStartDate()).isEqualTo(paidOn);
        assertThat(pending.getEndDate()).isEqualTo(paidOn.plusDays(30));
    }

    @Test
    void refusesToActivateTwice() {
        // A duplicate bank notification must not extend a plan for free.
        CustomerSubscription subscription = CustomerSubscription.createPendingPayment(1L, 2L);
        subscription.activate(LocalDate.of(2026, 7, 27), 30);

        assertThatThrownBy(() -> subscription.activate(LocalDate.of(2026, 8, 27), 30))
                .isInstanceOf(IllegalStateException.class);
    }
}
