package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.projetfilrougeapi.apifilrouge.DTO.CategoryResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CollectionModel<EntityModel<CategoryResponse>> getAllCategories() {

        List<EntityModel<CategoryResponse>> categories = this.categoryRepository.findAll().stream()
                .map(category -> {
                    CategoryResponse response = CategoryResponse.fromEntity(category);
                    return EntityModel.of(response,
                            linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel());
                })
                .collect(Collectors.toList());
        return CollectionModel.of(categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
    }

    public EntityModel<CategoryResponse> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        CategoryResponse response = CategoryResponse.fromEntity(category);
        return EntityModel.of(response,
                linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel());

    }
}
