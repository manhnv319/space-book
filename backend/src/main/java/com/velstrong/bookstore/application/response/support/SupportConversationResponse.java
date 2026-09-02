package com.velstrong.bookstore.application.response.support;

import com.velstrong.bookstore.domain.model.SupportConversation;
import com.velstrong.bookstore.domain.model.SupportMessage;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SupportConversationResponse(Long id, Long userId, String customerName, String customerEmail,
                                          Instant lastMessageAt, int staffUnreadCount, int customerUnreadCount,
                                          String lastMessagePreview, List<SupportMessageResponse> messages) {
    public static SupportConversationResponse from(SupportConversation conversation, String customerName,
                                                   String customerEmail, List<SupportMessage> messages,
                                                   Map<Long, List<SupportMessageAttachment>> attachments) {
        return new SupportConversationResponse(conversation.id(), conversation.userId(), customerName, customerEmail,
                SupportMessageResponse.toInstant(conversation.lastMessageAt()), conversation.staffUnreadCount(),
                conversation.customerUnreadCount(), conversation.lastMessagePreview(), messages.stream()
                .map(message -> SupportMessageResponse.from(message, attachments.getOrDefault(message.id(), List.of()))).toList());
    }

    public static SupportConversationResponse summary(SupportConversation conversation, String customerName, String customerEmail) {
        return from(conversation, customerName, customerEmail, List.of(), Map.of());
    }
}
