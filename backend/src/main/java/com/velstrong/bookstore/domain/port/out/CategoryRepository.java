package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
}
