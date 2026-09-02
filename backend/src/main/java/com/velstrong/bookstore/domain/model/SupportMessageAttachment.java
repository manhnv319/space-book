package com.velstrong.bookstore.domain.model;

import java.time.LocalDateTime;

/** Metadata for a normalized WebP image attached to a support message. */
public record SupportMessageAttachment(Long id, Long messageId, String imageUrl, String originalName,
                                       String contentType, LocalDateTime createdAt) { }
