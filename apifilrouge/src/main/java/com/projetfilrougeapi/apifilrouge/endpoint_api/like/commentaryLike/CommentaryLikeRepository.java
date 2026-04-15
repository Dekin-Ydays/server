package com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike;

import com.projetfilrougeapi.apifilrouge.DTO.CommentaryLikeProjection;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "commentaryLike", path = "commentaryLike", excerptProjection = CommentaryLikeProjection.class)
public interface CommentaryLikeRepository extends JpaRepository<CommentaryLike, Long>, JpaSpecificationExecutor<CommentaryLike> {
    CommentaryLike findByCommentary(Commentary commentary);
}
