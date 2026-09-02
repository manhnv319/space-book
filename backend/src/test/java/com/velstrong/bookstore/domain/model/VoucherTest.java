package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VoucherTest {

    @Test
    @DisplayName("PERCENTAGE discount is capped by maxDiscountAmount")
    void percentageDiscountIsCapped() {
        Voucher v = Voucher.create("SALE50", "Big sale", VoucherDiscountType.PERCENTAGE,
                50L, 100_000L, 0L, null, null, 100, 1);

        assertThat(v.calculateDiscount(1_000_000L)).isEqualTo(100_000L);
    }

    @Test
    @DisplayName("PERCENTAGE discount without cap applies the full percentage")
    void percentageDiscountWithoutCap() {
        Voucher v = Voucher.create("SALE10", "10% off", VoucherDiscountType.PERCENTAGE,
                10L, null, 0L, null, null, 100, 1);

        assertThat(v.calculateDiscount(500_000L)).isEqualTo(50_000L);
    }

    @Test
    @DisplayName("FIXED_AMOUNT discount is bounded by base amount")
    void fixedAmountDiscountBoundedByBase() {
        Voucher v = Voucher.create("FIXED30K", "30k off", VoucherDiscountType.FIXED_AMOUNT,
                30_000L, null, 0L, null, null, 100, 1);

        assertThat(v.calculateDiscount(20_000L)).isEqualTo(20_000L);
        assertThat(v.calculateDiscount(50_000L)).isEqualTo(30_000L);
    }

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        void returnsNullForActiveVoucherWithinWindow() {
            Voucher v = Voucher.create("OK", "ok", VoucherDiscountType.PERCENTAGE,
                    10L, null, 50_000L, null, null, 100, 1);
            assertThat(v.validate(100_000L, LocalDateTime.now())).isNull();
        }

        @Test
        void returnsInactiveWhenStatusIsZero() {
            Voucher v = Voucher.reconstitute(1L, "OFF", "off", null,
                    VoucherDiscountType.PERCENTAGE, 10L, null, 0L,
                    null, null, 100, 1, 0, (byte) 0);
            assertThat(v.validate(100_000L, LocalDateTime.now()))
                    .isEqualTo(VoucherValidationReason.INACTIVE);
        }

        @Test
        void returnsExpiredWhenNowAfterEndAt() {
            LocalDateTime past = LocalDateTime.now().minusDays(2);
            Voucher v = Voucher.create("EXP", "exp", VoucherDiscountType.PERCENTAGE,
                    10L, null, 0L, null, past, 100, 1);
            assertThat(v.validate(100_000L, LocalDateTime.now()))
                    .isEqualTo(VoucherValidationReason.EXPIRED);
        }

        @Test
        void returnsNotYetActiveWhenNowBeforeStartAt() {
            LocalDateTime future = LocalDateTime.now().plusDays(2);
            Voucher v = Voucher.create("FUT", "future", VoucherDiscountType.PERCENTAGE,
                    10L, null, 0L, future, null, 100, 1);
            assertThat(v.validate(100_000L, LocalDateTime.now()))
                    .isEqualTo(VoucherValidationReason.NOT_YET_ACTIVE);
        }

        @Test
        void returnsMinOrderNotMetWhenBaseBelowMin() {
            Voucher v = Voucher.create("MIN", "min", VoucherDiscountType.PERCENTAGE,
                    10L, null, 200_000L, null, null, 100, 1);
            assertThat(v.validate(50_000L, LocalDateTime.now()))
                    .isEqualTo(VoucherValidationReason.MIN_ORDER_NOT_MET);
        }
    }

    @Test
    @DisplayName("update mutates the mutable fields")
    void updateMutatesFields() {
        Voucher v = Voucher.create("UPD", "name", VoucherDiscountType.PERCENTAGE,
                10L, 50_000L, 0L, null, null, 100, 1);

        v.update("new name", "new desc", 20L, 100_000L, 0L, null, null, 200, 2);

        assertThat(v.getName()).isEqualTo("new name");
        assertThat(v.getDescription()).isEqualTo("new desc");
        assertThat(v.getDiscountValue()).isEqualTo(20L);
        assertThat(v.getMaxDiscountAmount()).isEqualTo(100_000L);
        assertThat(v.getUsageLimitTotal()).isEqualTo(200);
        assertThat(v.getUsageLimitPerUser()).isEqualTo(2);
    }

    @Test
    @DisplayName("update rejects non-positive discount value")
    void updateRejectsNonPositiveDiscount() {
        Voucher v = Voucher.create("UPD", "name", VoucherDiscountType.PERCENTAGE,
                10L, 50_000L, 0L, null, null, 100, 1);

        assertThatThrownBy(() -> v.update("n", "d", 0L, null, null, null, null, 1, 1))
                .isInstanceOf(InvalidOperationException.class);
        assertThatThrownBy(() -> v.update("n", "d", -5L, null, null, null, null, 1, 1))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("deactivate sets status to 0")
    void deactivate() {
        Voucher v = Voucher.create("D", "d", VoucherDiscountType.PERCENTAGE,
                10L, null, 0L, null, null, 1, 1);
        assertThat(v.isActive()).isTrue();
        v.deactivate();
        assertThat(v.isActive()).isFalse();
    }
}
