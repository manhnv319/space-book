package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReplaceOptions;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

abstract class MongoPersistenceSupport {

    private static final String COUNTERS_COLLECTION = "_mongo_sequences";

    protected final MongoTemplate mongo;

    protected MongoPersistenceSupport(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    protected <T> T save(String collection, T entity) {
        assignIdWhenMissing(collection, entity);
        if (findMongoVersionField(entity.getClass()) != null) {
            return saveVersioned(collection, entity);
        }
        return mongo.save(entity, collection);
    }

    protected <T> List<T> saveAll(String collection, List<T> entities) {
        return entities.stream().map(value -> save(collection, value)).toList();
    }

    protected <T> Optional<T> findById(String collection, Class<T> type, Long id) {
        return Optional.ofNullable(mongo.findById(id, type, collection));
    }

    protected <T> List<T> find(String collection, Class<T> type, Query query) {
        return mongo.find(query, type, collection);
    }

    protected <T> Optional<T> findOne(String collection, Class<T> type, Query query) {
        return Optional.ofNullable(mongo.findOne(query, type, collection));
    }

    protected <T> Optional<T> findAndModify(String collection, Class<T> type, Query query, Update update) {
        return Optional.ofNullable(mongo.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), type, collection));
    }

    protected <T> PageResult<T> page(String collection, Class<T> type, Query query, int page, int size) {
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), type, collection);
        List<T> content = mongo.find(query.limit(size).skip((long) page * size), type, collection);
        return PageResult.of(content, total);
    }

    protected boolean exists(String collection, Query query, Class<?> type) {
        return mongo.exists(query, type, collection);
    }

    protected long count(String collection, Query query, Class<?> type) {
        return mongo.count(query, type, collection);
    }

    protected void deleteById(String collection, Long id, Class<?> type) {
        mongo.remove(Query.query(Criteria.where("_id").is(id)), type, collection);
    }

    protected UpdateResult updateFirst(String collection, Query query, Update update, Class<?> type) {
        return mongo.updateFirst(query, update, type, collection);
    }

    protected static Query sorted(Query query, String field, Sort.Direction direction) {
        return query.with(Sort.by(direction, field));
    }

    private void assignIdWhenMissing(String collection, Object entity) {
        Field idField = ReflectionUtils.findField(entity.getClass(), "id");
        if (idField == null) return;
        ReflectionUtils.makeAccessible(idField);
        try {
            if (idField.get(entity) == null) idField.set(entity, nextId(collection));
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Cannot assign Mongo id for " + entity.getClass().getSimpleName(), ex);
        }
    }

    private long nextId(String collection) {
        Query query = Query.query(Criteria.where("_id").is(collection));
        Update update = new Update().inc("value", 1);
        Document sequence = mongo.findAndModify(query, update,
                FindAndModifyOptions.options().upsert(true).returnNew(true), Document.class, COUNTERS_COLLECTION);
        if (sequence == null || sequence.get("value") == null) {
            throw new IllegalStateException("Mongo sequence was not generated for " + collection);
        }
        return ((Number) sequence.get("value")).longValue();
    }

    private <T> T saveVersioned(String collection, T entity) {
        Field idField = ReflectionUtils.findField(entity.getClass(), "id");
        Field versionField = findMongoVersionField(entity.getClass());
        if (idField == null || versionField == null) return mongo.save(entity, collection);
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.makeAccessible(versionField);
        try {
            Object id = idField.get(entity);
            Document current = mongo.findOne(Query.query(Criteria.where("_id").is(id)),
                    Document.class, collection);
            if (current == null) {
                if (versionField.get(entity) == null) versionField.set(entity, 0L);
                return mongo.insert(entity, collection);
            }

            long currentVersion = current.get("version") instanceof Number number
                    ? number.longValue() : 0L;
            Object rawExpected = versionField.get(entity);
            long expectedVersion = rawExpected instanceof Number number
                    ? number.longValue() : currentVersion;
            if (rawExpected == null && !(current.get("version") instanceof Number)) {
                mongo.updateFirst(Query.query(Criteria.where("_id").is(id)),
                        new Update().set("version", currentVersion), Document.class, collection);
            }
            if (expectedVersion != currentVersion) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Stale Mongo version for " + entity.getClass().getSimpleName()
                                + " id=" + id + ": expected " + expectedVersion
                                + ", current " + currentVersion);
            }

            versionField.set(entity, currentVersion + 1);
            UpdateResult result = mongo.replace(
                    Query.query(new Criteria().andOperator(Criteria.where("_id").is(id),
                            Criteria.where("version").is(currentVersion))),
                    entity, new ReplaceOptions(), collection);
            if (result.getMatchedCount() == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Concurrent Mongo update for " + entity.getClass().getSimpleName()
                                + " id=" + id);
            }
            return entity;
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Cannot persist versioned Mongo entity "
                    + entity.getClass().getSimpleName(), ex);
        }
    }

    private Field findMongoVersionField(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equals("version")) return field;
        }
        return null;
    }
}
