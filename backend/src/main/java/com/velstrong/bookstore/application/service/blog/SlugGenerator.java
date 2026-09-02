package com.velstrong.bookstore.application.service.blog;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Pure, stateless slug builder — no DB access, no Spring. Uniqueness (the
 * "-2", "-3" suffixing) is handled by the calling service since it needs a
 * repository round trip; keeping that out of here keeps this unit-testable
 * with plain strings (see D-blog slug decision in the phase report).
 */
public final class SlugGenerator {

    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final int MAX_LENGTH = 200;

    private SlugGenerator() {
    }

    public static String generate(String input) {
        if (input == null) return "";

        String normalized = input.replace('đ', 'd').replace('Đ', 'D');
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = COMBINING_MARKS.matcher(normalized).replaceAll("");
        normalized = normalized.toLowerCase();
        normalized = NON_ALPHANUMERIC.matcher(normalized).replaceAll("-");
        normalized = trimDashes(normalized);

        if (normalized.length() > MAX_LENGTH) {
            normalized = trimDashes(normalized.substring(0, MAX_LENGTH));
        }
        return normalized;
    }

    private static String trimDashes(String value) {
        int start = 0;
        int end = value.length();
        while (start < end && value.charAt(start) == '-') start++;
        while (end > start && value.charAt(end - 1) == '-') end--;
        return value.substring(start, end);
    }
}
