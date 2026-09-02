package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Category;
import com.velstrong.bookstore.domain.port.out.CategoryRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCategoryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("postgres & !mongodb")
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;

    public CategoryPersistenceAdapter(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    private Category toDomain(CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getSlug());
    }
}
