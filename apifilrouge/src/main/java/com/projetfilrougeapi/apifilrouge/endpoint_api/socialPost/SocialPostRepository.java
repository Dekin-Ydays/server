package com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost;

import com.projetfilrougeapi.apifilrouge.DTO.SocialPostProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(collectionResourceRel = "socialPost", path = "socialPost", excerptProjection = SocialPostProjection.class)
public interface SocialPostRepository extends JpaRepository<SocialPost, Long>, JpaSpecificationExecutor<SocialPost> {
}
