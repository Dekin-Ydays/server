package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/categories")
    public CollectionModel<EntityModel<Category>> getAllCategories() {
        List<EntityModel<Category>> categories = categoryRepository.findAll().stream()
                .map(category -> EntityModel.of(category,
                        linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
    }

   @GetMapping("/categories/{id}")
   public EntityModel<Category> getCategoryById(@PathVariable Long id) {
       Category category = categoryRepository.findById(id)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

       return EntityModel.of(category,
               linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel());
   }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Category> addPlace(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);

        return EntityModel.of(savedCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(savedCategory.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
    }
}
