package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("mongodb")
class MongoVersionedEntityTest {

    private static final String COLLECTION = "mongo_version_test";

    @Autowired
    private MongoTemplate mongo;

    @AfterEach
    void cleanUp() {
        mongo.dropCollection(COLLECTION);
        mongo.remove(Query.query(Criteria.where("_id").is(COLLECTION)), Document.class, "_mongo_sequences");
    }

    @Test
    void staleVersionIsRejectedByMongoSave() {
        MongoPersistenceSupport persistence = new MongoPersistenceSupport(mongo) { };

        OrderJpaEntity inserted = new OrderJpaEntity();
        inserted.setUserId(1L);
        inserted.setOrderCode("MONGO-VERSION-TEST");
        inserted.setOrderType("PURCHASE");
        inserted.setStatus("PENDING");
        inserted.setPaymentStatus("PENDING");
        inserted.setPaymentMethod("BANK_TRANSFER");
        persistence.save(COLLECTION, inserted);

        assertThat(inserted.getVersion()).isZero();

        OrderJpaEntity current = new OrderJpaEntity();
        current.setId(inserted.getId());
        current.setUserId(1L);
        current.setOrderCode("MONGO-VERSION-TEST");
        current.setOrderType("PURCHASE");
        current.setStatus("PAID");
        current.setPaymentStatus("PAID");
        current.setPaymentMethod("BANK_TRANSFER");
        persistence.save(COLLECTION, current);

        assertThat(current.getVersion()).isEqualTo(1L);

        OrderJpaEntity stale = new OrderJpaEntity();
        stale.setId(inserted.getId());
        stale.setVersion(0L);
        stale.setUserId(1L);
        stale.setOrderCode("MONGO-VERSION-TEST");
        stale.setOrderType("PURCHASE");
        stale.setStatus("CANCELLED");
        stale.setPaymentStatus("PAID");
        stale.setPaymentMethod("BANK_TRANSFER");

        assertThatThrownBy(() -> persistence.save(COLLECTION, stale))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
