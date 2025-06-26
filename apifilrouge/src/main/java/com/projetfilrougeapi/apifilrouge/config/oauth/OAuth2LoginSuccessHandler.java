package com.projetfilrougeapi.apifilrouge.config.oauth;

import com.projetfilrougeapi.apifilrouge.config.JwtService;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * This method is triggered automatically by Spring Security
     * when a user successfully authenticates via OAuth2.
     *
     * It retrieves the authenticated OAuth2 user, looks up the corresponding
     * user in the database, generates a JWT for them, and redirects the client
     * to the front-end with the token as a URL parameter.
     *
     * @param request        the incoming HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object containing the authenticated user's details
     * @throws IOException      if an input or output exception occurs during redirection
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("OAuth2 user could not be located in the database following authentication and potential creation/update."));

        String jwtToken = jwtService.generateToken(user);

        // Redirect the user to the front-end with the token
        response.sendRedirect("http://localhost:3000/auth/callback?token=" + jwtToken);
    }
}