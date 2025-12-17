package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.projetfilrougeapi.apifilrouge.DTO.CategoryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping("/{id}")
    public EntityModel<CategoryResponse> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<CategoryResponse>> getAllCategories() {
        return categoryService.getAllCategories();
    }
}