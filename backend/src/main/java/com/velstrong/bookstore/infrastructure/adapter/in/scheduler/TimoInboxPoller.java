package com.velstrong.bookstore.infrastructure.adapter.in.scheduler;

import com.velstrong.bookstore.application.command.payment.BankTransferNotification;
import com.velstrong.bookstore.domain.port.in.payment.ConfirmBankTransferUseCase;
import com.velstrong.bookstore.infrastructure.adapter.out.external.TimoCreditEmailParser;
import com.velstrong.bookstore.infrastructure.adapter.out.external.TimoMailAuthenticity;
import com.velstrong.bookstore.infrastructure.config.BankTransferProperties;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.SearchTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

/**
 * Inbound adapter: polls the mailbox Timo sends balance notifications to and
 * hands each genuine credit to {@link ConfirmBankTransferUseCase}.
 *
 * This is a driving adapter — it sits in `adapter/in` because it starts a flow,
 * the same way a controller does; IMAP just happens to be the transport.
 *
 * Notes on behaviour:
 * <ul>
 *   <li>The mailbox is opened read-only. This inbox belongs to a person, and
 *       the poller has no business marking their mail read or deleting it.</li>
 *   <li>The IMAP search is narrowed to the bank's address and a short lookback
 *       window, so unrelated personal mail is never fetched.</li>
 *   <li>Idempotency lives in {@code ConfirmBankTransferService}, keyed on
 *       Message-ID, which is why re-reading the same mail is harmless and why
 *       read-only access is enough.</li>
 *   <li>Only credits carrying a payment reference are forwarded. A credit with
 *       no reference cannot belong to an order, and forwarding it would only
 *       write noise into the reconciliation table.</li>
 * </ul>
 */
@Component
public class TimoInboxPoller {

    private static final Logger log = LoggerFactory.getLogger(TimoInboxPoller.class);

    private final BankTransferProperties properties;
    private final TimoCreditEmailParser parser;
    private final TimoMailAuthenticity authenticity;
    private final ConfirmBankTransferUseCase confirm;

    public TimoInboxPoller(BankTransferProperties properties, TimoCreditEmailParser parser,
                           TimoMailAuthenticity authenticity, ConfirmBankTransferUseCase confirm) {
        this.properties = properties;
        this.parser = parser;
        this.authenticity = authenticity;
        this.confirm = confirm;
    }

    @Scheduled(fixedDelayString = "${app.bank-transfer.imap.poll-delay-ms:15000}")
    public void poll() {
        BankTransferProperties.Imap imap = properties.imap();
        if (!imap.isComplete()) return;

        Store store = null;
        Folder folder = null;
        try {
            store = connect(imap);
            folder = store.getFolder(imap.folder());
            folder.open(Folder.READ_ONLY);
            for (Message message : folder.search(recentMailFrom(imap))) {
                handle(message, imap);
            }
        } catch (Exception e) {
            // Never log the exception's connect URL — it can carry the password.
            log.warn("Timo inbox poll failed: {}", e.getClass().getSimpleName());
        } finally {
            close(folder, store);
        }
    }

    private Store connect(BankTransferProperties.Imap imap) throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", imap.host());
        props.put("mail.imaps.port", String.valueOf(imap.port()));
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.checkserveridentity", "true");
        Store store = Session.getInstance(props).getStore("imaps");
        store.connect(imap.host(), imap.port(), imap.username(), imap.password());
        return store;
    }

    private SearchTerm recentMailFrom(BankTransferProperties.Imap imap) {
        Date since = Date.from(Instant.now().minus(imap.lookbackMinutes(), ChronoUnit.MINUTES));
        return new AndTerm(new FromStringTerm(imap.expectedSender()),
                new ReceivedDateTerm(ComparisonTerm.GE, since));
    }

    private void handle(Message message, BankTransferProperties.Imap imap) {
        try {
            String messageId = firstHeader(message, "Message-ID");
            if (messageId == null || messageId.isBlank()) return;

            if (!authenticity.isGenuine(firstHeader(message, "From"),
                    message.getHeader("Authentication-Results"), imap.expectedSender(), imap.authServId())) {
                log.warn("Discarded a message claiming to be from {} that failed authentication", imap.expectedSender());
                return;
            }

            Optional<TimoCreditEmailParser.TimoCredit> parsed = parser.parse(htmlBodyOf(message));
            if (parsed.isEmpty()) return;

            TimoCreditEmailParser.TimoCredit credit = parsed.get();
            if (credit.paymentReference() == null) return;

            confirm.confirm(new BankTransferNotification(messageId, null, credit.paymentReference(),
                    credit.amount(), credit.occurredAt()));
        } catch (Exception e) {
            log.warn("Skipped a Timo notification that could not be read: {}", e.getClass().getSimpleName());
        }
    }

    private String firstHeader(Message message, String name) throws Exception {
        String[] values = message.getHeader(name);
        return values == null || values.length == 0 ? null : values[0];
    }

    /** Timo wraps the notification in multipart/mixed with a single text/html part. */
    private String htmlBodyOf(Part part) throws Exception {
        if (part.isMimeType("text/html")) return (String) part.getContent();
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String html = htmlBodyOf(multipart.getBodyPart(i));
                if (html != null) return html;
            }
        }
        return null;
    }

    private void close(Folder folder, Store store) {
        try {
            if (folder != null && folder.isOpen()) folder.close(false);
        } catch (Exception ignored) {
            // closing a read-only folder cannot lose data
        }
        try {
            if (store != null && store.isConnected()) store.close();
        } catch (Exception ignored) {
            // ditto
        }
    }
}
