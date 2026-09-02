package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 6, 19);

    private Rental activeRental(LocalDate plannedReturn) {
        return Rental.create(1L, 100L, 7L, RentalTermUnit.MONTH, 1,
                50_000L, TODAY, plannedReturn);
    }

    @Nested
    @DisplayName("returnBook")
    class ReturnBook {

        @Test
        void marksReturnedAndClearsLateDaysWhenOnTime() {
            Rental r = activeRental(TODAY.plusDays(5));
            r.returnBook(TODAY);
            assertThat(r.getStatus()).isEqualTo(RentalStatus.RETURNED);
            assertThat(r.getActualReturnDate()).isEqualTo(TODAY);
            assertThat(r.getLateDays()).isEqualTo(0);
        }

        @Test
        void computesLateDaysWhenReturnedAfterPlannedDate() {
            Rental r = activeRental(TODAY.minusDays(3));
            r.returnBook(TODAY);
            assertThat(r.getStatus()).isEqualTo(RentalStatus.RETURNED);
            assertThat(r.getLateDays()).isEqualTo(3);
        }

        @Test
        void rejectsReturnOnAlreadyReturnedRental() {
            Rental r = activeRental(TODAY);
            r.returnBook(TODAY);
            assertThatThrownBy(() -> r.returnBook(TODAY))
                    .isInstanceOf(InvalidOperationException.class);
        }
    }

    @Test
    @DisplayName("calculateLateFee multiplies lateDays by feePerDay")
    void calculateLateFee() {
        Rental r = activeRental(TODAY.minusDays(4));
        r.returnBook(TODAY);
        r.calculateLateFee(10_000L, TODAY);
        assertThat(r.getLateFeeAmount()).isEqualTo(40_000L);
    }

    @Test
    @DisplayName("calculateRefund = deposit - late - damage, never below zero")
    void calculateRefundAmount() {
        Rental r = activeRental(TODAY);
        r.applyDamageFee(10_000L);
        r.setLateFeeAmount(5_000L);
        assertThat(r.calculateRefundAmount()).isEqualTo(35_000L);

        r.applyDamageFee(100_000L);
        r.setLateFeeAmount(0L);
        assertThat(r.calculateRefundAmount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("isOverdue reflects today > plannedReturnDate for active rentals")
    void isOverdue() {
        Rental active = activeRental(TODAY.plusDays(1));
        assertThat(active.isOverdue(TODAY)).isFalse();

        Rental overdue = activeRental(TODAY.minusDays(1));
        assertThat(overdue.isOverdue(TODAY)).isTrue();

        overdue.returnBook(TODAY);
        assertThat(overdue.isOverdue(TODAY)).isFalse();
    }
}
