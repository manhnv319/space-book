package com.velstrong.bookstore.infrastructure.adapter.in.rest.category;

import com.velstrong.bookstore.application.response.category.CategoryResponse;
import com.velstrong.bookstore.domain.port.in.category.GetAllCategoriesUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final GetAllCategoriesUseCase getAllCategoriesUseCase;

    public CategoryController(GetAllCategoriesUseCase getAllCategoriesUseCase) {
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(getAllCategoriesUseCase.getAll()));
    }
}
