package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        config.exposeIdsFor(
                SocialPost.class,
                Like.class,
                SocialPostLike.class,
                CommentaryLike.class,
                Commentary.class,
                Category.class,
                Video.class
        );

        config.getProjectionConfiguration()
                .addProjection(SocialPostProjection.class)
                .addProjection(LikeProjection.class)
                .addProjection(SocialPostLikeProjection.class)
                .addProjection(CommentaryLikeProjection.class)
                .addProjection(CommentaryProjection.class)
                .addProjection(CategoryProjection.class)
                .addProjection(VideoProjection.class);
    }
}