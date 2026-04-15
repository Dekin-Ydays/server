package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.projetfilrougeapi.apifilrouge.DTO.CategoryProjection;
import com.projetfilrougeapi.apifilrouge.DTO.CommentaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories", excerptProjection = CategoryProjection.class)
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

}
