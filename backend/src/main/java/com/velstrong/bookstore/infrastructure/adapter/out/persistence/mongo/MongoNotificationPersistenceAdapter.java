package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.out.NotificationRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserNotificationJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoNotificationPersistenceAdapter extends MongoPersistenceSupport implements NotificationRepository {

    private static final String COLLECTION = "user_notifications";

    public MongoNotificationPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public UserNotification save(UserNotification value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<UserNotification> findByIdAndUserId(Long id, Long userId) { return findOne(COLLECTION, UserNotificationJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("_id").is(id), Criteria.where("userId").is(userId)))).map(this::toDomain); }

    @Override
    public PageResult<UserNotification> findByUserId(Long userId, int page, int size) {
        Query query = Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<UserNotification> values = find(COLLECTION, UserNotificationJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), UserNotificationJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    @Override public long countUnreadByUserId(Long userId) { return count(COLLECTION, Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("readAt").is(null))), UserNotificationJpaEntity.class); }

    @Override public void markAllReadByUserId(Long userId) { mongo.updateMulti(Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("readAt").is(null))), new Update().set("readAt", LocalDateTime.now()), UserNotificationJpaEntity.class, COLLECTION); }

    private UserNotification toDomain(UserNotificationJpaEntity e) { return new UserNotification(e.getId(), e.getUserId(), NotificationType.valueOf(e.getType()), e.getTitle(), e.getBody(), e.getTargetPath(), e.getReadAt(), e.getCreatedAt()); }
    private UserNotificationJpaEntity toEntity(UserNotification d) { UserNotificationJpaEntity e = new UserNotificationJpaEntity(); e.setId(d.id()); e.setUserId(d.userId()); e.setType(d.type().name()); e.setTitle(d.title()); e.setBody(d.body()); e.setTargetPath(d.targetPath()); e.setReadAt(d.readAt()); e.setCreatedAt(d.createdAt()); return e; }
}
