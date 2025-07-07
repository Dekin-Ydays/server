package com.projetfilrougeapi.apifilrouge.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter that forces HTTPS usage by adding appropriate headers
 * for reverse proxies. This filter ensures that HATEOAS links
 * are generated with the correct protocol (HTTPS).
 */
@Component
@Order(1)
public class HttpsForwardingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Check if the request comes from a reverse proxy with HTTPS
        String forwardedProto = httpRequest.getHeader("X-Forwarded-Proto");
        String forwardedHost = httpRequest.getHeader("X-Forwarded-Host");
        String forwardedPort = httpRequest.getHeader("X-Forwarded-Port");
        
        if ("https".equals(forwardedProto)) {
            // Create a request wrapper that forces HTTPS
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
                            return 443; // Default HTTPS port
                        }
                    }
                    return 443; // Default HTTPS port
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