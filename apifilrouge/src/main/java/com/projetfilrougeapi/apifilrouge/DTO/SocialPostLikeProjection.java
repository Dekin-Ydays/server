package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "socialPostLikeDetails", types = { SocialPostLike.class })
public interface SocialPostLikeProjection {
    Long getId();

    @Value("#{target.socialPost.id}")
    Long getSocialPostId();

    @Value("#{target.socialPostLikeSum}")
    Integer getSocialPostLikes();
}
