package com.velstrong.bookstore.infrastructure.adapter.out.external;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the "Thông báo thay đổi số dư tài khoản" mail Timo sends on every
 * balance change.
 *
 * The body is HTML written for humans, so this reads the sentence rather than
 * any structured field:
 *
 *   Tài khoản Spend Account vừa tăng 30.000 VND vào 26/07/2026 16:44.
 *   Mô tả: NGUYEN VAN MANH chuyen tien den NGUYEN VAN MANH - 9021003210798.
 *
 * Only credits are returned. A debit ("vừa giảm") uses the same template, and
 * treating one as an incoming payment would mark an order paid when money left
 * the account.
 *
 * The description is read only to pull the payment reference out of it; the
 * caller never stores it, because for unrelated personal transactions it is
 * private information that has no business in this database.
 */
@Component
public class TimoCreditEmailParser {

    /** Anchored on "tăng" so debits fall through. `[\s\S]` because the HTML wraps mid-sentence. */
    private static final Pattern CREDIT = Pattern.compile(
            "vừa\\s+tăng\\s+([\\d.,]+)\\s*VND\\s+vào\\s+(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2})",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern DESCRIPTION = Pattern.compile(
            "Mô\\s*tả\\s*:\\s*([^<]*)", Pattern.CASE_INSENSITIVE);

    /** Matches the reference minted by CreateBankTransferPaymentService. */
    private static final Pattern REFERENCE = Pattern.compile("\\bTQ[0-9A-Z]{4,30}\\b");

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");

    private static final DateTimeFormatter OCCURRED_AT = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    /**
     * @param html decoded text/html body of the notification
     * @return the credit, or empty when the mail is a debit or does not match the template
     */
    public Optional<TimoCredit> parse(String html) {
        if (html == null || html.isBlank()) return Optional.empty();
        String text = toPlainText(html);

        Matcher credit = CREDIT.matcher(text);
        if (!credit.find()) return Optional.empty();

        Long amount = parseAmount(credit.group(1));
        if (amount == null) return Optional.empty();

        LocalDateTime occurredAt = parseOccurredAt(credit.group(2));
        if (occurredAt == null) return Optional.empty();

        return Optional.of(new TimoCredit(amount, occurredAt, findReference(text)));
    }

    private String toPlainText(String html) {
        // Entities first: a literal "&lt;b&gt;" in the description must not become a tag.
        String text = html.replace("&nbsp;", " ").replace("&amp;", "&");
        text = HTML_TAG.matcher(text).replaceAll(" ");
        return text.replaceAll("\\s+", " ").trim();
    }

    private Long parseAmount(String raw) {
        // VND is printed with thousands separators and no minor unit: "30.000" -> 30000.
        String digits = raw.replaceAll("[.,\\s]", "");
        if (digits.isEmpty() || digits.length() > 18) return null;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime parseOccurredAt(String raw) {
        try {
            return LocalDateTime.parse(raw.replaceAll("\\s+", " ").trim(), OCCURRED_AT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String findReference(String text) {
        Matcher description = DESCRIPTION.matcher(text);
        // Scan the description when present, otherwise the whole body — some
        // banks reformat the memo into the headline sentence.
        String haystack = description.find() ? description.group(1) : text;
        Matcher reference = REFERENCE.matcher(haystack.toUpperCase());
        return reference.find() ? reference.group() : null;
    }

    /** Amount in VND, when the transfer happened, and the payment reference if the sender included one. */
    public record TimoCredit(long amount, LocalDateTime occurredAt, String paymentReference) {}
}
