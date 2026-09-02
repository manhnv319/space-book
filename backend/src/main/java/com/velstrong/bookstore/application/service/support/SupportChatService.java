package com.velstrong.bookstore.application.service.support;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.support.SupportConversationResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.*;
import com.velstrong.bookstore.domain.model.enums.support.SupportSender;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;
import com.velstrong.bookstore.domain.port.in.support.SupportChatUseCase;
import com.velstrong.bookstore.domain.port.out.SupportAttachmentStorage;
import com.velstrong.bookstore.domain.port.out.SupportChatRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupportChatService implements SupportChatUseCase {
    private final SupportChatRepository chats;
    private final SupportAttachmentStorage attachmentStorage;
    private final UserRepository users;
    private final NotificationUseCase notifications;

    public SupportChatService(SupportChatRepository chats) {
        this(chats, (conversationId, messageId, uploads) -> List.of(), null, null);
    }

    @Autowired
    public SupportChatService(SupportChatRepository chats, SupportAttachmentStorage attachmentStorage, UserRepository users, NotificationUseCase notifications) {
        this.chats = chats;
        this.attachmentStorage = attachmentStorage;
        this.users = users;
        this.notifications = notifications;
    }

    @Override
    public SupportConversationResponse myConversation(Long userId) {
        return chats.findByUserId(userId).map(conversation -> {
            SupportConversation current = conversation.customerUnreadCount() == 0 ? conversation : chats.save(conversation.markCustomerRead());
            return response(current);
        }).orElseGet(() -> emptyConversation(userId));
    }

    @Override
    public SupportConversationResponse sendAsCustomer(Long userId, String body, List<SupportAttachmentUpload> attachments) {
        SupportConversation conversation = chats.findByUserId(userId).orElseGet(() -> chats.save(SupportConversation.openFor(userId)));
        return append(conversation, SupportSender.CUSTOMER, userId, body, attachments);
    }

    public SupportConversationResponse sendAsCustomer(Long userId, String body) {
        return sendAsCustomer(userId, body, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupportConversationResponse> queue(int page, int size) {
        PageResult<SupportConversation> result = chats.findRecent(page, size);
        return PagedResponse.of(result.content().stream().map(this::summary).toList(), page, size, result.totalElements());
    }

    @Override
    public SupportConversationResponse staffView(Long conversationId, Long staffUserId) {
        SupportConversation conversation = chats.findById(conversationId).orElseThrow(() -> new EntityNotFoundException("Conversation", conversationId));
        SupportConversation claimed = conversation.claimIfUnassigned(staffUserId);
        return response(chats.save(claimed.staffUnreadCount() == 0 ? claimed : claimed.markStaffRead()));
    }

    @Override
    public SupportConversationResponse replyAsStaff(Long conversationId, Long staffUserId, String body, List<SupportAttachmentUpload> attachments) {
        SupportConversation conversation = chats.findById(conversationId).orElseThrow(() -> new EntityNotFoundException("Conversation", conversationId));
        SupportConversation claimed = conversation.claimIfUnassigned(staffUserId);
        if (!staffUserId.equals(claimed.assignedStaffUserId())) throw new InvalidOperationException("Hội thoại đang được một nhân viên khác xử lý.");
        return append(claimed, SupportSender.STAFF, staffUserId, body, attachments);
    }

    public SupportConversationResponse replyAsStaff(Long conversationId, Long staffUserId, String body) {
        return replyAsStaff(conversationId, staffUserId, body, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public long staffUnreadConversationCount() {
        return chats.countStaffUnreadConversations();
    }

    private SupportConversationResponse append(SupportConversation conversation, SupportSender sender, Long senderUserId,
                                               String body, List<SupportAttachmentUpload> attachments) {
        List<SupportAttachmentUpload> safeAttachments = attachments == null ? List.of() : attachments;
        try {
            SupportMessage message = chats.saveMessage(SupportMessage.create(conversation.id(), sender, senderUserId, body, !safeAttachments.isEmpty()));
            chats.saveAttachments(attachmentStorage.store(conversation.id(), message.id(), safeAttachments));
            SupportConversation saved = chats.save(conversation.touched(sender, message.body(), !safeAttachments.isEmpty()));
            sendNotification(saved, sender, message.id(), message.body(), !safeAttachments.isEmpty());
            return response(saved);
        } catch (IllegalArgumentException exception) {
            throw new InvalidOperationException(exception.getMessage());
        }
    }

    private SupportConversationResponse response(SupportConversation conversation) {
        List<SupportMessage> messages = chats.findMessages(conversation.id());
        Map<Long, List<SupportMessageAttachment>> attachments = chats.findAttachments(messages.stream().map(SupportMessage::id).toList())
                .stream().collect(Collectors.groupingBy(SupportMessageAttachment::messageId));
        Customer customer = customer(conversation.userId());
        return SupportConversationResponse.from(conversation, customer.name(), customer.email(), messages, attachments);
    }

    private SupportConversationResponse summary(SupportConversation conversation) {
        Customer customer = customer(conversation.userId());
        return SupportConversationResponse.summary(conversation, customer.name(), customer.email());
    }

    private SupportConversationResponse emptyConversation(Long userId) {
        Customer customer = customer(userId);
        return SupportConversationResponse.from(new SupportConversation(null, userId, null, null, 0, 0, "", null), customer.name(), customer.email(), List.of(), Map.of());
    }

    private void sendNotification(SupportConversation conversation, SupportSender sender, Long messageId, String body, boolean hasAttachments) {
        if (notifications == null) return;
        String preview = body == null || body.isBlank() ? (hasAttachments ? "Đã gửi một ảnh" : "") : body;
        if (sender == SupportSender.CUSTOMER) {
            notifications.notify(conversation.assignedStaffUserId(), NotificationType.CHAT, "Tin nhắn hỗ trợ mới", preview, "/admin/ho-tro/" + conversation.id());
        } else {
            notifications.notify(conversation.userId(), NotificationType.CHAT, "Nhà sách đã phản hồi", preview, "/?supportMessage=" + messageId + "#support");
        }
    }

    private Customer customer(Long userId) {
        if (users == null) return new Customer("Người dùng #" + userId, "");
        return users.findById(userId).map(user -> new Customer(
                user.getFullname() == null || user.getFullname().isBlank() ? user.getUsername() : user.getFullname(), user.getEmail()
        )).orElse(new Customer("Người dùng #" + userId, ""));
    }

    private record Customer(String name, String email) { }
}
