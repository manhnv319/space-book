package com.velstrong.bookstore.application.response.support;

import com.velstrong.bookstore.domain.model.SupportMessageAttachment;

public record SupportMessageAttachmentResponse(Long id, String imageUrl, String originalName, String contentType) {
    public static SupportMessageAttachmentResponse from(SupportMessageAttachment attachment) {
        return new SupportMessageAttachmentResponse(attachment.id(), attachment.imageUrl(), attachment.originalName(), attachment.contentType());
    }
}
