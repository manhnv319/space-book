package com.velstrong.bookstore.infrastructure.adapter.out.external;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TimoCreditEmailParserTest {

    private final TimoCreditEmailParser parser = new TimoCreditEmailParser();

    /** Shaped like the real notification: HTML, tags splitting the sentence. */
    private static String body(String direction, String amount, String description) {
        return """
                <html><body>
                <p><span class="customer-name">NGUYEN VAN MANH</span> thân mến,</p>
                <p><p>Tài khoản Spend Account vừa %s %s VND vào 26/07/2026 16:44.
                Số dư hiện tại: 35.000 VND.</p>
                <p>Mô tả: %s.</p></p>
                </body></html>
                """.formatted(direction, amount, description);
    }

    @Test
    void readsAmountTimeAndReferenceFromACredit() {
        Optional<TimoCreditEmailParser.TimoCredit> credit =
                parser.parse(body("tăng", "30.000", "NGUYEN VAN MANH chuyen tien TQ1A2B3C4 den NGUYEN VAN MANH"));

        assertThat(credit).isPresent();
        assertThat(credit.get().amount()).isEqualTo(30_000L);
        assertThat(credit.get().occurredAt()).isEqualTo(LocalDateTime.of(2026, 7, 26, 16, 44));
        assertThat(credit.get().paymentReference()).isEqualTo("TQ1A2B3C4");
    }

    @Test
    void ignoresDebits() {
        // Same template, opposite direction. Treating this as income would mark
        // an order paid when money left the account.
        assertThat(parser.parse(body("giảm", "30.000", "TQ1A2B3C4"))).isEmpty();
    }

    @Test
    void readsCreditWithoutReferenceSoCallerCanDecideWhatToDo() {
        Optional<TimoCreditEmailParser.TimoCredit> credit =
                parser.parse(body("tăng", "30.000", "NGUYEN VAN MANH chuyen tien den NGUYEN VAN MANH - 9021003210798"));

        assertThat(credit).isPresent();
        assertThat(credit.get().paymentReference()).isNull();
    }

    @Test
    void parsesThousandsSeparatorsWithoutInventingDecimals() {
        assertThat(parser.parse(body("tăng", "1.250.000", "TQXXXXX")).orElseThrow().amount())
                .isEqualTo(1_250_000L);
    }

    @Test
    void returnsEmptyForUnrelatedMailAndRubbish() {
        assertThat(parser.parse("<html><body><p>Khuyến mãi tháng 7</p></body></html>")).isEmpty();
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse(null)).isEmpty();
    }

    @Test
    void doesNotTreatEscapedMarkupInTheDescriptionAsTags() {
        Optional<TimoCreditEmailParser.TimoCredit> credit =
                parser.parse(body("tăng", "30.000", "&lt;b&gt;TQ9Z8Y7X&lt;/b&gt;"));

        assertThat(credit).isPresent();
        assertThat(credit.get().paymentReference()).isEqualTo("TQ9Z8Y7X");
    }
}
