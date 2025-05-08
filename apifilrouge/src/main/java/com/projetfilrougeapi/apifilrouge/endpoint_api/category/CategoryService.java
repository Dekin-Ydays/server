package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CollectionModel<EntityModel<Category>> getAllCategories() {
        List<EntityModel<Category>> categories = categoryRepository.findAll().stream()
                .map(category -> EntityModel.of(category,
                        linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
    }

    public EntityModel<Category> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel());
    }

    public EntityModel<Category> addCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);

        return EntityModel.of(savedCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(savedCategory.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
    }
    public EntityModel<Category> updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existingCategory.getName() != null && !existingCategory.getName().equals(category.getName()) ) {
            existingCategory.setName(category.getName());
        }
        if (existingCategory.getDescription() != null && !existingCategory.getDescription().equals(category.getDescription()) ) {
            existingCategory.setDescription(category.getDescription());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);

        return EntityModel.of(updatedCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(updatedCategory.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
    }
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        categoryRepository.delete(category);
    }
}
