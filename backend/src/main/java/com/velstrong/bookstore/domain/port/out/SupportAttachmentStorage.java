package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.SupportAttachmentUpload;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;

import java.util.List;

/** Storage boundary: application code does not know filesystem paths or WebP tooling. */
public interface SupportAttachmentStorage {
    List<SupportMessageAttachment> store(Long conversationId, Long messageId, List<SupportAttachmentUpload> uploads);
}
