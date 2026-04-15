package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "commentaryLikeDetails", types = { CommentaryLike.class })
public interface CommentaryLikeProjection {
    Long getId();

    @Value("#{target.commentary.id}")
    Long getCommentary();

    @Value("#{target.commentaryLikeSum}")
    Integer getCommentaryLikes();
}
