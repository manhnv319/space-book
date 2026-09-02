package com.velstrong.bookstore.domain.port.in.support;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.support.SupportConversationResponse;
import com.velstrong.bookstore.domain.model.SupportAttachmentUpload;

import java.util.List;

public interface SupportChatUseCase {
    SupportConversationResponse myConversation(Long userId);
    SupportConversationResponse sendAsCustomer(Long userId, String body, List<SupportAttachmentUpload> attachments);
    PagedResponse<SupportConversationResponse> queue(int page, int size);
    SupportConversationResponse staffView(Long conversationId, Long staffUserId);
    SupportConversationResponse replyAsStaff(Long conversationId, Long staffUserId, String body, List<SupportAttachmentUpload> attachments);
    long staffUnreadConversationCount();
}
