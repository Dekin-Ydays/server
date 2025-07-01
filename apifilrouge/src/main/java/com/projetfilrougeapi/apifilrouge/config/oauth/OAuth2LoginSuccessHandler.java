package com.projetfilrougeapi.apifilrouge.config.oauth;

import com.projetfilrougeapi.apifilrouge.config.JwtService;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        log.info("OAuth2 authentication successful for email: {}", email);
        log.info("OAuth2 user attributes: {}", oauthUser.getAttributes());

        // Verify if te user is in the db
        var userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isEmpty()) {
            log.warn("User not found in database for email: {}. Creating user as fallback...", email);
            user = createUserFromOAuth2(oauthUser);
        } else {
            user = userOptional.get();
            log.info("User found in database: {}", user.getEmail());
        }

        String jwtToken = jwtService.generateToken(user);
        log.info("JWT token generated successfully for user: {}", user.getEmail());

        // Redirect the user to the front-end with the token
        response.sendRedirect("http://localhost:3000/auth/callback?token=" + jwtToken);
    }

    /**
     * Fallback method to create user if CustomOAuth2UserService doesn't work
     */
    private User createUserFromOAuth2(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String imageUrl = oauth2User.getAttribute("picture");

        log.info("Creating user from OAuth2 data: email={}, firstName={}, lastName={}", email, firstName, lastName);

        User newUser = User.builder()
                .firstName(firstName != null ? firstName : "Unknown")
                .lastName(lastName != null ? lastName : "User")
                .email(email)
                .pseudo(email)
                .imageUrl(imageUrl)
                .role(Role.User)
                .provider(AuthProvider.GOOGLE)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();

        User savedUser = userRepository.saveAndFlush(newUser);
        log.info("User created successfully in fallback method: {}", savedUser.getEmail());
        return savedUser;
    }
}