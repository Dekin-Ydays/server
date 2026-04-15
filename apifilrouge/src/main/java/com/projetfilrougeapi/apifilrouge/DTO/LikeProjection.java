package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "likeDetails", types = { Like.class })
public interface LikeProjection {
    Long getId();

    @Value("#{target.user != null ? target.user.pseudo : null}")
    String getUserName();

    @Value("#{target.socialPost != null ? target.socialPost.id : null}")
    Long getSocialPostId();

    @Value("#{target.commentary != null ? target.commentary.id : null}")
    Long getCommentaryId();

    @Value("#{target.socialPostLike != null ? target.socialPostLike.socialPostLikeSum : 0}")
    Integer getSocialPostTotalLikes();

    @Value("#{target.commentaryLike != null ? target.commentaryLike.commentaryLikeSum : 0}")
    Integer getCommentTotalLikes();
}
