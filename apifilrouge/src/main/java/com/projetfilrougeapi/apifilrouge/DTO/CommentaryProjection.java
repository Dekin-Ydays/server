package com.projetfilrougeapi.apifilrouge.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "commentaryDetails", types = { Commentary.class })
public interface CommentaryProjection {

    Long getId();

    String getContent();

    @Value("#{target.socialPost.id}")
    Long getSocialPostId();

    @Value("#{target.user.pseudo}")
    String getUserName();

    @Value("#{target.commentaryLike != null ? target.commentaryLike.commentaryLikeSum : 0}")
    Integer getLikeCount();
}