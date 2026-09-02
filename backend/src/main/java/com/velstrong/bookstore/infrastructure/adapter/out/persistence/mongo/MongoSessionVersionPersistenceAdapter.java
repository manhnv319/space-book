package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@Profile("mongodb & !postgres")
public class MongoSessionVersionPersistenceAdapter implements SessionVersionRepository {

    private static final String COLLECTION = "user_session_versions";
    private final MongoTemplate mongo;

    public MongoSessionVersionPersistenceAdapter(MongoTemplate mongo) { this.mongo = mongo; }
    @Override public long currentVersion(Long userId) { Document value = mongo.findById(userId, Document.class, COLLECTION); return value == null || value.get("version") == null ? 0L : ((Number) value.get("version")).longValue(); }
    @Override public long incrementVersion(Long userId) { Document value = mongo.findAndModify(Query.query(Criteria.where("_id").is(userId)), new Update().inc("version", 1), FindAndModifyOptions.options().upsert(true).returnNew(true), Document.class, COLLECTION); return ((Number) value.get("version")).longValue(); }
}
