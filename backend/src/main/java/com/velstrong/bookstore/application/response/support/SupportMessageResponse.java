package com.velstrong.bookstore.application.response.support;

import com.velstrong.bookstore.domain.model.SupportMessage;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;
import com.velstrong.bookstore.domain.model.enums.support.SupportSender;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record SupportMessageResponse(Long id, SupportSender sender, String body, Instant sentAt,
                                     List<SupportMessageAttachmentResponse> attachments) {
    public static SupportMessageResponse from(SupportMessage message) {
        return from(message, List.of());
    }

    public static SupportMessageResponse from(SupportMessage message, List<SupportMessageAttachment> attachments) {
        return new SupportMessageResponse(message.id(), message.sender(), message.body(), toInstant(message.createdAt()),
                attachments.stream().map(SupportMessageAttachmentResponse::from).toList());
    }

    static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault()).toInstant();
    }
}
