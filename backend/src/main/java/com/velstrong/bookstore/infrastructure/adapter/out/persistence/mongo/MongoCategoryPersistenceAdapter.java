package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.Category;
import com.velstrong.bookstore.domain.port.out.CategoryRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("mongodb & !postgres")
public class MongoCategoryPersistenceAdapter extends MongoPersistenceSupport implements CategoryRepository {

    private static final String COLLECTION = "categories";

    public MongoCategoryPersistenceAdapter(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public List<Category> findAll() {
        return find(COLLECTION, CategoryJpaEntity.class, new Query().with(Sort.by(Sort.Direction.ASC, "name")))
                .stream().map(value -> new Category(value.getId(), value.getName(), value.getSlug())).toList();
    }
}
