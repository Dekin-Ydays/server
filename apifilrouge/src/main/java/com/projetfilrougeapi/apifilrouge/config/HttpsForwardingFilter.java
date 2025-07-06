package com.projetfilrougeapi.apifilrouge.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtre qui force l'utilisation de HTTPS en ajoutant les en-têtes appropriés
 * pour les reverse proxies comme ngrok. Ce filtre s'assure que les liens HATEOAS
 * sont générés avec le bon protocole (HTTPS).
 */
@Component
@Order(1)
public class HttpsForwardingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Vérifie si la requête vient d'un reverse proxy avec HTTPS
        String forwardedProto = httpRequest.getHeader("X-Forwarded-Proto");
        String forwardedHost = httpRequest.getHeader("X-Forwarded-Host");
        String forwardedPort = httpRequest.getHeader("X-Forwarded-Port");
        
        if ("https".equals(forwardedProto)) {
            // Crée une wrapper de requête qui force HTTPS
            HttpServletRequestWrapper httpsRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getScheme() {
                    return "https";
                }
                
                @Override
                public String getServerName() {
                    return forwardedHost != null ? forwardedHost : super.getServerName();
                }
                
                @Override
                public int getServerPort() {
                    if (forwardedPort != null) {
                        try {
                            return Integer.parseInt(forwardedPort);
                        } catch (NumberFormatException e) {
                            return 443; // Port HTTPS par défaut
                        }
                    }
                    return 443; // Port HTTPS par défaut
                }
                
                @Override
                public boolean isSecure() {
                    return true;
                }
            };
            
            chain.doFilter(httpsRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
} 