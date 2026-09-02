package com.velstrong.bookstore.infrastructure.adapter.out.external;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Decides whether a notification mail really came from Timo.
 *
 * This matters more than anything else in the reconciliation path: confirming a
 * payment from an email means anyone who can drop a message into the mailbox
 * could otherwise mint "your balance increased by X, memo TQ..." and walk away
 * with a free order. Checking the {@code From} header alone is worthless — it
 * is attacker-controlled text.
 *
 * So the decision rests on the {@code Authentication-Results} header that our
 * own receiving server stamps on delivery. Only the first one is read: a
 * receiver prepends its verdict, and any header the sender tried to forge ends
 * up below it. The header must come from the expected authserv-id and report
 * both DKIM and DMARC passing for the bank's domain — DMARC is what ties the
 * visible From address to the authenticated one.
 */
@Component
public class TimoMailAuthenticity {

    public boolean isGenuine(String from, String[] authenticationResults, String expectedSender, String authServId) {
        if (!fromMatches(from, expectedSender)) return false;
        if (authenticationResults == null || authenticationResults.length == 0) return false;

        // First header only — see class comment.
        String verdict = authenticationResults[0].toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        String domain = senderDomain(expectedSender);
        if (domain == null) return false;

        if (!verdict.startsWith(authServId.toLowerCase(Locale.ROOT))) return false;
        return verdict.contains("dkim=pass") && verdict.contains("header.i=@" + domain)
                && verdict.contains("dmarc=pass") && verdict.contains("header.from=" + domain);
    }

    /** Accepts both "support@timo.vn" and "Timo Support <support@timo.vn>". */
    private boolean fromMatches(String from, String expectedSender) {
        if (from == null || expectedSender == null || expectedSender.isBlank()) return false;
        return from.toLowerCase(Locale.ROOT).contains(expectedSender.toLowerCase(Locale.ROOT));
    }

    private String senderDomain(String expectedSender) {
        int at = expectedSender.lastIndexOf('@');
        if (at < 0 || at == expectedSender.length() - 1) return null;
        return expectedSender.substring(at + 1).toLowerCase(Locale.ROOT);
    }
}
