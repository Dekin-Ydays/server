package com.projetfilrougeapi.apifilrouge.endpoint_api.commentary;

import com.projetfilrougeapi.apifilrouge.DTO.CommentaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "commentary", path = "commentary", excerptProjection = CommentaryProjection.class)
public interface CommentaryRepository extends JpaRepository<Commentary, Long>, JpaSpecificationExecutor<Commentary> {
}