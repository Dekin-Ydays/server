package com.projetfilrougeapi.apifilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for HATEOAS that forces HTTPS in generated links.
 * This configuration is particularly useful when hosting with ngrok
 * or other reverse proxies that use HTTPS.
 */
@Configuration
public class HateoasConfig implements WebMvcConfigurer {

    /**
     * Configure the link relation provider for HATEOAS.
     * Uses EvoInflector for better relation name generation.
     *
     * @return The configured link relation provider
     */
    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return new EvoInflectorLinkRelationProvider();
    }
} 