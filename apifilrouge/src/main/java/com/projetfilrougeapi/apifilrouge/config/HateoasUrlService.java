package com.projetfilrougeapi.apifilrouge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service qui gère la génération des URLs HATEOAS avec le bon protocole.
 * Ce service s'assure que les liens sont générés en HTTPS quand nécessaire.
 */
@Service
public class HateoasUrlService {

    @Value("${app.force-https:false}")
    private boolean forceHttps;

    @Value("${app.base-url:}")
    private String baseUrl;

    /**
     * Détermine si la requête actuelle doit utiliser HTTPS.
     *
     * @return true si HTTPS doit être utilisé, false sinon
     */
    public boolean shouldUseHttps() {
        if (forceHttps) {
            return true;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // Vérifie les en-têtes de forwarding
            String forwardedProto = request.getHeader("X-Forwarded-Proto");
            if ("https".equals(forwardedProto)) {
                return true;
            }
            
            // Vérifie si la requête est sécurisée
            return request.isSecure();
        }
        
        return false;
    }

    /**
     * Obtient l'URL de base pour la génération des liens HATEOAS.
     *
     * @return L'URL de base
     */
    public String getBaseUrl() {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            String scheme = shouldUseHttps() ? "https" : request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            
            // Construit l'URL de base
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder.append(scheme).append("://").append(serverName);
            
            // Ajoute le port seulement si ce n'est pas le port standard
            if ((scheme.equals("http") && serverPort != 80) || 
                (scheme.equals("https") && serverPort != 443)) {
                baseUrlBuilder.append(":").append(serverPort);
            }
            
            return baseUrlBuilder.toString();
        }
        
        return "http://localhost:8090"; // Fallback par défaut
    }
} 