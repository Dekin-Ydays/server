package com.projetfilrougeapi.apifilrouge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service that manages HATEOAS URL generation with the correct protocol.
 * This service ensures that links are generated using HTTPS when needed.
 */
@Service
public class HateoasUrlService {

    @Value("${app.force-https:false}")
    private boolean forceHttps;

    @Value("${app.base-url:}")
    private String baseUrl;

    /**
     * Determines if the current request should use HTTPS.
     *
     * @return true if HTTPS should be used, false otherwise
     */
    public boolean shouldUseHttps() {
        if (forceHttps) {
            return true;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // Check forwarding headers
            String forwardedProto = request.getHeader("X-Forwarded-Proto");
            if ("https".equals(forwardedProto)) {
                return true;
            }
            
            // Check if the request is secure
            return request.isSecure();
        }
        
        return false;
    }

    /**
     * Gets the base URL for HATEOAS link generation.
     *
     * @return The base URL
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
            
            // Build the base URL
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder.append(scheme).append("://").append(serverName);
            
            // Add the port only if it's not the standard port
            if ((scheme.equals("http") && serverPort != 80) || 
                (scheme.equals("https") && serverPort != 443)) {
                baseUrlBuilder.append(":").append(serverPort);
            }
            
            return baseUrlBuilder.toString();
        }
        
        return "http://localhost:8090"; // Default fallback
    }
} 