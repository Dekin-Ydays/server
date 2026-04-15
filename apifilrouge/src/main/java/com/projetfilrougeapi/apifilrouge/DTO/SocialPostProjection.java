package com.projetfilrougeapi.apifilrouge.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "socialPostDetails", types = { SocialPost.class })
public interface SocialPostProjection {

    Long getId();

    String getSocialPostDescription();

    @Value("#{target.category.categoryName}")
    String getCategoryName();

    @Value("#{target.user.pseudo}")
    String getUserName();

    @Value("#{target.video.url}")
    String getVideoUrl();

    @Value("#{target.socialPostLike != null ? target.socialPostLike.socialPostLikeSum : 0}")
    Integer getLikeCount();

    @Value("#{target.commentaries}")
    List<Commentary> getComments();
}
