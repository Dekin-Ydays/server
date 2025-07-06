package com.projetfilrougeapi.apifilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration pour HATEOAS qui force l'utilisation de HTTPS dans les liens générés.
 * Cette configuration est particulièrement utile lors de l'hébergement avec ngrok
 * ou d'autres reverse proxies qui utilisent HTTPS.
 */
@Configuration
public class HateoasConfig implements WebMvcConfigurer {

    /**
     * Configure le fournisseur de relations de liens pour HATEOAS.
     * Utilise EvoInflector pour une meilleure génération des noms de relations.
     *
     * @return Le fournisseur de relations de liens configuré
     */
    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return new EvoInflectorLinkRelationProvider();
    }
} 