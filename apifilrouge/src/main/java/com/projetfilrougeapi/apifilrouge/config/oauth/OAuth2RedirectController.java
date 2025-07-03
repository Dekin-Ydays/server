package com.projetfilrougeapi.apifilrouge.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
public class OAuth2RedirectController {

    /**
     * Custom endpoint to initiate OAuth2 login with a dynamic "state" (redirect URI).
     * Stores the redirect URI in the session and forwards to Spring Security's login endpoint.
     *
     * @param redirectUri The frontend URL to redirect to after successful login
     */
    @GetMapping("/oauth2/custom-google")
    public void redirectToGoogleOAuth(
            @RequestParam("state") String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("Saving custom redirect URI in session: {}", redirectUri);

        // Store the redirect URI in session to retrieve later after successful login
        request.getSession().setAttribute("redirect_uri_override", redirectUri);

        // Redirect to Spring Security's built-in Google OAuth2 endpoint
        response.sendRedirect("/oauth2/authorization/google");
    }
}
