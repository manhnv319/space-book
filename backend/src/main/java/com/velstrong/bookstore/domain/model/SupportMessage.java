package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.support.SupportSender;

import java.time.LocalDateTime;

/** A text message, optionally accompanied by normalized image attachments. */
public record SupportMessage(Long id, Long conversationId, SupportSender sender, Long senderUserId,
                             String body, LocalDateTime createdAt) {
    public static final int MAX_BODY_LENGTH = 2000;

    public static SupportMessage create(Long conversationId, SupportSender sender, Long senderUserId, String body) {
        return create(conversationId, sender, senderUserId, body, false);
    }

    public static SupportMessage create(Long conversationId, SupportSender sender, Long senderUserId,
                                        String body, boolean hasAttachments) {
        String trimmed = body == null ? "" : body.strip();
        if (trimmed.isEmpty() && !hasAttachments) throw new IllegalArgumentException("Message body is empty");
        if (trimmed.length() > MAX_BODY_LENGTH) throw new IllegalArgumentException("Message body is too long");
        return new SupportMessage(null, conversationId, sender, senderUserId, trimmed, LocalDateTime.now());
    }
}
