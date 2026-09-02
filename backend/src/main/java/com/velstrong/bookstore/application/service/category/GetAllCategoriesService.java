package com.velstrong.bookstore.application.service.category;

import com.velstrong.bookstore.application.response.category.CategoryResponse;
import com.velstrong.bookstore.domain.port.in.category.GetAllCategoriesUseCase;
import com.velstrong.bookstore.domain.port.out.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllCategoriesService implements GetAllCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public GetAllCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
