package com.velstrong.bookstore.domain.port.in.category;

import com.velstrong.bookstore.application.response.category.CategoryResponse;

import java.util.List;

public interface GetAllCategoriesUseCase {
    List<CategoryResponse> getAll();
}
