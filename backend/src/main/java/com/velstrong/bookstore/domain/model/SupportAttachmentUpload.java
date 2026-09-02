package com.velstrong.bookstore.domain.model;

/** Raw image accepted at the REST boundary; never persisted as-is. */
public record SupportAttachmentUpload(String originalName, String contentType, byte[] content) {
    public SupportAttachmentUpload {
        content = content == null ? new byte[0] : content.clone();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
