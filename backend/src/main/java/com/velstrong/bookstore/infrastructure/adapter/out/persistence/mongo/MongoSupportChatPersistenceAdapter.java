package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.SupportConversation;
import com.velstrong.bookstore.domain.model.SupportMessage;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;
import com.velstrong.bookstore.domain.model.enums.support.SupportSender;
import com.velstrong.bookstore.domain.port.out.SupportChatRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportConversationJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportMessageAttachmentJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.SupportMessageJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoSupportChatPersistenceAdapter extends MongoPersistenceSupport implements SupportChatRepository {

    private static final String CONVERSATIONS = "support_conversations";
    private static final String MESSAGES = "support_messages";
    private static final String ATTACHMENTS = "support_message_attachments";

    public MongoSupportChatPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Optional<SupportConversation> findByUserId(Long userId) { return findOne(CONVERSATIONS, SupportConversationJpaEntity.class, Query.query(Criteria.where("userId").is(userId))).map(this::toDomain); }
    @Override public Optional<SupportConversation> findById(Long id) { return findById(CONVERSATIONS, SupportConversationJpaEntity.class, id).map(this::toDomain); }

    @Override
    public SupportConversation save(SupportConversation value) {
        SupportConversationJpaEntity e = value.id() == null ? new SupportConversationJpaEntity()
                : findById(CONVERSATIONS, SupportConversationJpaEntity.class, value.id()).orElseGet(SupportConversationJpaEntity::new);
        e.setId(value.id()); e.setUserId(value.userId()); e.setCreatedAt(value.createdAt()); e.setLastMessageAt(value.lastMessageAt());
        e.setStaffUnreadCount(value.staffUnreadCount()); e.setCustomerUnreadCount(value.customerUnreadCount()); e.setLastMessagePreview(value.lastMessagePreview()); e.setAssignedStaffUserId(value.assignedStaffUserId());
        return toDomain(save(CONVERSATIONS, e));
    }

    @Override public List<SupportMessage> findMessages(Long conversationId) { return find(MESSAGES, SupportMessageJpaEntity.class, Query.query(Criteria.where("conversationId").is(conversationId)).with(Sort.by(Sort.Direction.ASC, "createdAt"))).stream().map(this::toDomain).toList(); }
    @Override public SupportMessage saveMessage(SupportMessage value) { return toDomain(save(MESSAGES, toEntity(value))); }

    @Override
    public List<SupportMessageAttachment> findAttachments(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return List.of();
        return find(ATTACHMENTS, SupportMessageAttachmentJpaEntity.class, Query.query(Criteria.where("messageId").in(messageIds)).with(Sort.by(Sort.Direction.ASC, "_id"))).stream().map(this::toDomain).toList();
    }

    @Override public void saveAttachments(List<SupportMessageAttachment> values) { saveAll(ATTACHMENTS, values.stream().map(this::toEntity).toList()); }

    @Override
    public PageResult<SupportConversation> findRecent(int page, int size) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "lastMessageAt"));
        List<SupportConversation> values = find(CONVERSATIONS, SupportConversationJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), SupportConversationJpaEntity.class, CONVERSATIONS);
        return PageResult.of(values, total);
    }

    @Override public long countStaffUnreadConversations() { return count(CONVERSATIONS, Query.query(Criteria.where("staffUnreadCount").gt(0)), SupportConversationJpaEntity.class); }

    private SupportConversation toDomain(SupportConversationJpaEntity e) { return new SupportConversation(e.getId(), e.getUserId(), e.getCreatedAt(), e.getLastMessageAt(), e.getStaffUnreadCount(), e.getCustomerUnreadCount(), e.getLastMessagePreview(), e.getAssignedStaffUserId()); }
    private SupportMessage toDomain(SupportMessageJpaEntity e) { return new SupportMessage(e.getId(), e.getConversationId(), SupportSender.valueOf(e.getSender()), e.getSenderUserId(), e.getBody(), e.getCreatedAt()); }
    private SupportMessageAttachment toDomain(SupportMessageAttachmentJpaEntity e) { return new SupportMessageAttachment(e.getId(), e.getMessageId(), e.getImageUrl(), e.getOriginalName(), e.getContentType(), e.getCreatedAt()); }
    private SupportMessageJpaEntity toEntity(SupportMessage d) { SupportMessageJpaEntity e = new SupportMessageJpaEntity(); e.setId(d.id()); e.setConversationId(d.conversationId()); e.setSender(d.sender().name()); e.setSenderUserId(d.senderUserId()); e.setBody(d.body()); e.setCreatedAt(d.createdAt()); return e; }
    private SupportMessageAttachmentJpaEntity toEntity(SupportMessageAttachment d) { SupportMessageAttachmentJpaEntity e = new SupportMessageAttachmentJpaEntity(); e.setId(d.id()); e.setMessageId(d.messageId()); e.setImageUrl(d.imageUrl()); e.setOriginalName(d.originalName()); e.setContentType(d.contentType()); e.setCreatedAt(d.createdAt()); return e; }
}
