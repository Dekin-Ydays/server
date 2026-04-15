package com.projetfilrougeapi.apifilrouge.endpoint_api.like.handler;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLikeRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Like.class)
public class LikeEventHandler {

    @Autowired
    private SocialPostLikeRepository socialPostLikeRepository;

    @Autowired
    private CommentaryLikeRepository commentaryLikeRepository;

    @HandleBeforeCreate
    public void handleLikeBeforeCreate(Like like) {
        if (like.getSocialPost() != null) {
            SocialPostLike stats = socialPostLikeRepository.findBySocialPost(like.getSocialPost());

            if (stats != null) {
                stats.setSocialPostLikeSum(stats.getSocialPostLikeSum() + 1);

                SocialPostLike saved = socialPostLikeRepository.saveAndFlush(stats);

                like.setSocialPostLike(saved);
            }
        }
        if (like.getCommentary() != null) {
            CommentaryLike stats = commentaryLikeRepository.findByCommentary(like.getCommentary());

            if (stats != null) {
                stats.setCommentaryLikeSum(stats.getCommentaryLikeSum() + 1);

                CommentaryLike saved = commentaryLikeRepository.saveAndFlush(stats);

                like.setCommentaryLike(saved);
            }
        }
    }
}