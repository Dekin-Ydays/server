package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.handler.LikeEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public LikeEventHandler likeEventHandler() {
        return new LikeEventHandler();
    }
}