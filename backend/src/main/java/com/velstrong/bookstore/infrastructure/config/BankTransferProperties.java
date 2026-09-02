package com.velstrong.bookstore.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bank-transfer")
public record BankTransferProperties(
        String bankName,
        String bankBin,
        String accountNumber,
        String accountName,
        int expiryMinutes,
        Imap imap
) {
    public BankTransferProperties {
        expiryMinutes = expiryMinutes > 0 ? expiryMinutes : 30;
        imap = imap == null ? Imap.disabled() : imap;
    }

    public boolean isBankConfigured() {
        return bankBin != null && bankBin.matches("\\d{6}") && accountNumber != null && accountNumber.matches("[0-9]{6,20}");
    }

    public record Imap(boolean enabled, String host, int port, String folder, String username, String password,
                       long pollDelayMs, String expectedSender, String authServId, int lookbackMinutes) {

        public Imap {
            pollDelayMs = pollDelayMs > 0 ? pollDelayMs : 15000;
            expectedSender = blankTo(expectedSender, "support@timo.vn");
            // The authserv-id of the mailbox provider; only its verdict is trusted.
            authServId = blankTo(authServId, "mx.google.com");
            lookbackMinutes = lookbackMinutes > 0 ? lookbackMinutes : 180;
        }

        public static Imap disabled() {
            return new Imap(false, "", 993, "INBOX", "", "", 15000, null, null, 0);
        }

        public boolean isComplete() {
            return enabled && host != null && !host.isBlank() && port > 0 && folder != null && !folder.isBlank()
                    && username != null && !username.isBlank() && password != null && !password.isBlank();
        }

        private static String blankTo(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }
}
