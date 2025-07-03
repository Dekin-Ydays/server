package com.projetfilrougeapi.apifilrouge.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth2")
public class OAuth2RedirectController {

    private final Set<String> allowedUris;

    public OAuth2RedirectController(
            @Value("${app.oauth2.allowed-redirect-uris}") String allowedUrisRaw
    ) {
        this.allowedUris = Arrays.stream(allowedUrisRaw.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    /**
     * Redirects to Google OAuth2 authorization endpoint, after validating the frontend's redirect URI.
     *
     * @param redirectUri The URL to which the frontend wants to be redirected after login.
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @throws IOException If sending the redirect fails.
     */
    @GetMapping("/authorize/google")
    public void redirectToGoogle(
            @RequestParam("redirect_uri") String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        boolean allowed = allowedUris.stream().anyMatch(redirectUri::startsWith);
        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Redirection URL non autorisée !");
            return;
        }
        request.getSession().setAttribute("oauth2_redirect_uri", redirectUri);
        response.sendRedirect("/oauth2/authorization/google");
    }
}