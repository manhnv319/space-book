package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.SupportConversation;
import com.velstrong.bookstore.domain.model.SupportMessage;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;

import java.util.List;
import java.util.Optional;

public interface SupportChatRepository {
    Optional<SupportConversation> findByUserId(Long userId);
    Optional<SupportConversation> findById(Long conversationId);
    SupportConversation save(SupportConversation conversation);
    List<SupportMessage> findMessages(Long conversationId);
    SupportMessage saveMessage(SupportMessage message);
    List<SupportMessageAttachment> findAttachments(List<Long> messageIds);
    void saveAttachments(List<SupportMessageAttachment> attachments);
    PageResult<SupportConversation> findRecent(int page, int size);
    long countStaffUnreadConversations();
}
