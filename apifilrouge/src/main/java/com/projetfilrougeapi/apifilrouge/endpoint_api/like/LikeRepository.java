package com.projetfilrougeapi.apifilrouge.endpoint_api.like;

import com.projetfilrougeapi.apifilrouge.DTO.LikeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "like", path = "like", excerptProjection = LikeProjection.class)
public interface LikeRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {

}
