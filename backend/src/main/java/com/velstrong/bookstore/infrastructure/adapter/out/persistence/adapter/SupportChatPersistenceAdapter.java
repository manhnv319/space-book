package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.*;
import com.velstrong.bookstore.domain.model.enums.support.SupportSender;
import com.velstrong.bookstore.domain.port.out.SupportChatRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.*;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class SupportChatPersistenceAdapter implements SupportChatRepository {
    private final JpaSupportConversationRepository conversations;
    private final JpaSupportMessageRepository messages;
    private final JpaSupportMessageAttachmentRepository attachments;

    public SupportChatPersistenceAdapter(JpaSupportConversationRepository conversations, JpaSupportMessageRepository messages,
                                         JpaSupportMessageAttachmentRepository attachments) {
        this.conversations = conversations;
        this.messages = messages;
        this.attachments = attachments;
    }

    @Override public Optional<SupportConversation> findByUserId(Long userId) { return conversations.findByUserId(userId).map(this::toDomain); }
    @Override public Optional<SupportConversation> findById(Long conversationId) { return conversations.findById(conversationId).map(this::toDomain); }

    @Override
    public SupportConversation save(SupportConversation conversation) {
        SupportConversationJpaEntity entity = conversation.id() == null ? new SupportConversationJpaEntity()
                : conversations.findById(conversation.id()).orElseGet(SupportConversationJpaEntity::new);
        entity.setId(conversation.id());
        entity.setUserId(conversation.userId());
        entity.setCreatedAt(conversation.createdAt());
        entity.setLastMessageAt(conversation.lastMessageAt());
        entity.setStaffUnreadCount(conversation.staffUnreadCount());
        entity.setCustomerUnreadCount(conversation.customerUnreadCount());
        entity.setLastMessagePreview(conversation.lastMessagePreview());
        entity.setAssignedStaffUserId(conversation.assignedStaffUserId());
        return toDomain(conversations.save(entity));
    }

    @Override public List<SupportMessage> findMessages(Long conversationId) { return messages.findByConversationIdOrderByCreatedAtAsc(conversationId).stream().map(this::toDomain).toList(); }
    @Override public SupportMessage saveMessage(SupportMessage message) { return toDomain(messages.save(toEntity(message))); }

    @Override
    public List<SupportMessageAttachment> findAttachments(List<Long> messageIds) {
        if (messageIds.isEmpty()) return List.of();
        return attachments.findByMessageIdInOrderByIdAsc(messageIds).stream().map(this::toDomain).toList();
    }

    @Override
    public void saveAttachments(List<SupportMessageAttachment> values) {
        attachments.saveAll(values.stream().map(this::toEntity).toList());
    }

    @Override
    public PageResult<SupportConversation> findRecent(int page, int size) {
        Page<SupportConversationJpaEntity> found = conversations.findAllByOrderByLastMessageAtDesc(PageRequest.of(page, size));
        return new PageResult<>(found.getContent().stream().map(this::toDomain).toList(), found.getTotalElements());
    }

    @Override public long countStaffUnreadConversations() { return conversations.countByStaffUnreadCountGreaterThan(0); }

    private SupportConversation toDomain(SupportConversationJpaEntity e) {
        return new SupportConversation(e.getId(), e.getUserId(), e.getCreatedAt(), e.getLastMessageAt(),
                e.getStaffUnreadCount(), e.getCustomerUnreadCount(), e.getLastMessagePreview(), e.getAssignedStaffUserId());
    }

    private SupportMessage toDomain(SupportMessageJpaEntity e) { return new SupportMessage(e.getId(), e.getConversationId(), SupportSender.valueOf(e.getSender()), e.getSenderUserId(), e.getBody(), e.getCreatedAt()); }
    private SupportMessageAttachment toDomain(SupportMessageAttachmentJpaEntity e) { return new SupportMessageAttachment(e.getId(), e.getMessageId(), e.getImageUrl(), e.getOriginalName(), e.getContentType(), e.getCreatedAt()); }

    private SupportMessageJpaEntity toEntity(SupportMessage message) {
        SupportMessageJpaEntity entity = new SupportMessageJpaEntity();
        entity.setConversationId(message.conversationId()); entity.setSender(message.sender().name());
        entity.setSenderUserId(message.senderUserId()); entity.setBody(message.body()); entity.setCreatedAt(message.createdAt());
        return entity;
    }

    private SupportMessageAttachmentJpaEntity toEntity(SupportMessageAttachment value) {
        SupportMessageAttachmentJpaEntity entity = new SupportMessageAttachmentJpaEntity();
        entity.setMessageId(value.messageId()); entity.setImageUrl(value.imageUrl()); entity.setOriginalName(value.originalName());
        entity.setContentType(value.contentType()); entity.setCreatedAt(value.createdAt());
        return entity;
    }
}
