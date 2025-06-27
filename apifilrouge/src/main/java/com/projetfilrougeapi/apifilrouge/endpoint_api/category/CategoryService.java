package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
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
    private final EventRepository eventRepository;

    public CategoryService(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    public CollectionModel<EntityModel<Category>> getAllCategories() {
        List<EntityModel<Category>> categories = categoryRepository.findAll().stream()
                .map(category -> EntityModel.of(category,
                        linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null,null,null, null, null, null, null, null)).withRel("events"));

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

    public EntityModel<Category> updateCategory(Long id, Category categoryRequest) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (categoryRequest.getName() != null) {
            existingCategory.setName(categoryRequest.getName());
        }

        if (categoryRequest.getDescription() != null) {
            existingCategory.setDescription(categoryRequest.getDescription());
        }

        if (categoryRequest.getKey() != null) {
            existingCategory.setKey(categoryRequest.getKey());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);

        return EntityModel.of(updatedCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(updatedCategory.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie non trouvée avec l'ID: " + id));

        if (!category.getEvents().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de supprimer cette catégorie : elle est encore associée à un ou plusieurs événements.");
        }

        categoryRepository.delete(category);
    }

}
