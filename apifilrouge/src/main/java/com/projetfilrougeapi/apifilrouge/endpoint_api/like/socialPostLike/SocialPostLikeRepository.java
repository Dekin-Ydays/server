package com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike;

import com.projetfilrougeapi.apifilrouge.DTO.SocialPostLikeProjection;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "socialPostLike", path = "socialPostLike", excerptProjection = SocialPostLikeProjection.class)
public interface SocialPostLikeRepository  extends JpaRepository<SocialPostLike, Long>, JpaSpecificationExecutor<SocialPostLike> {
    SocialPostLike findBySocialPost(SocialPost socialPost);
}
