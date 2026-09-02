package com.velstrong.bookstore.infrastructure.adapter.out.external;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is the only thing standing between the reconciler and a forged
 * "you have been paid" email, so every rejection path is pinned here.
 */
class TimoMailAuthenticityTest {

    private static final String SENDER = "support@timo.vn";
    private static final String AUTHSERV = "mx.google.com";

    private final TimoMailAuthenticity authenticity = new TimoMailAuthenticity();

    private static final String GENUINE = "mx.google.com; dkim=pass header.i=@timo.vn header.s=mail "
            + "header.b=3E4jG9yX; spf=pass (google.com: domain of support@timo.vn designates 18.140.167.8 as "
            + "permitted sender) smtp.mailfrom=support@timo.vn; dmarc=pass (p=QUARANTINE sp=QUARANTINE dis=NONE) "
            + "header.from=timo.vn";

    private boolean check(String from, String... results) {
        return authenticity.isGenuine(from, results, SENDER, AUTHSERV);
    }

    @Test
    void acceptsTheRealNotification() {
        assertThat(check("Timo Support <support@timo.vn>", GENUINE)).isTrue();
    }

    @Test
    void rejectsMailWithNoAuthenticationVerdict() {
        assertThat(check("Timo Support <support@timo.vn>")).isFalse();
        assertThat(authenticity.isGenuine("Timo Support <support@timo.vn>", null, SENDER, AUTHSERV)).isFalse();
    }

    @Test
    void rejectsFailedOrMissingDkim() {
        assertThat(check("Timo Support <support@timo.vn>", GENUINE.replace("dkim=pass", "dkim=fail"))).isFalse();
        assertThat(check("Timo Support <support@timo.vn>", GENUINE.replace("dmarc=pass", "dmarc=fail"))).isFalse();
    }

    @Test
    void rejectsAPassThatBelongsToAnotherDomain() {
        // Attacker's own domain authenticates perfectly well — for their domain.
        assertThat(check("Timo Support <support@timo.vn>",
                GENUINE.replace("header.i=@timo.vn", "header.i=@attacker.example")
                        .replace("header.from=timo.vn", "header.from=attacker.example"))).isFalse();
    }

    @Test
    void rejectsAVerdictStampedBySomeoneOtherThanOurMailProvider() {
        assertThat(check("Timo Support <support@timo.vn>", GENUINE.replace("mx.google.com", "mx.attacker.example")))
                .isFalse();
    }

    @Test
    void readsOnlyTheTopHeaderSoAForgedOneUnderneathCannotWin() {
        String forged = GENUINE;
        String real = "mx.google.com; dkim=fail; spf=fail; dmarc=fail header.from=timo.vn";
        // The receiver prepends its own verdict, so index 0 is the trustworthy one.
        assertThat(check("Timo Support <support@timo.vn>", real, forged)).isFalse();
    }

    @Test
    void rejectsAnotherSender() {
        assertThat(check("Someone Else <noreply@example.com>", GENUINE)).isFalse();
        assertThat(check(null, GENUINE)).isFalse();
    }
}
