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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> allowedRedirectUris;

    @Autowired
    public OAuth2LoginSuccessHandler(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${app.oauth2.allowed-redirect-uris}") String allowedUris
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.allowedRedirectUris = Arrays.stream(allowedUris.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    /**
     * Custom handler for successful OAuth2 authentication.
     * <p>
     * On successful Google login, this handler:
     * <ul>
     *     <li>Checks or creates the user in the database</li>
     *     <li>Generates a JWT for the user</li>
     *     <li>Retrieves the redirect_uri from the OAuth2 request</li>
     *     <li>Validates that the redirect_uri is authorized</li>
     *     <li>Redirects the user to their frontend with the token as a query parameter</li>
     * </ul>
     *
     * @param request        The incoming HTTP request.
     * @param response       The HTTP response.
     * @param authentication The Spring Authentication object (logged-in user).
     * @throws IOException      On I/O error during redirection.
     * @throws ServletException On servlet-related error.
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        log.info("OAuth2 authentication successful for email: {}", email);

        // Check user or create if not exists
        var userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseGet(() -> {
            log.warn("User not found for email: {}. Creating...", email);
            return createUserFromOAuth2(oauthUser);
        });

        String jwtToken = jwtService.generateToken(user);
        log.info("JWT token generated for: {}", user.getEmail());

        // Recover redirect_uri from the OAuth2AuthorizationRequest in session (set in /authorize/google)
        String redirectUri = (String) request.getSession().getAttribute("oauth2_redirect_uri");
        if (redirectUri == null || redirectUri.isBlank()) {
            log.error("No redirect_uri in session! Rejecting for safety.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid redirect_uri.");
            return;
        }
        log.info("Found redirect_uri from session: {}", redirectUri);

        // Validate
        boolean allowed = allowedRedirectUris.stream().anyMatch(redirectUri::startsWith);
        if (!allowed) {
            log.error("Redirection not allowed: {}", redirectUri);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Redirection URL non autorisée !");
            return;
        }

        // Clear session attribute (for security)
        request.getSession().removeAttribute("oauth2_redirect_uri");

        String finalRedirectUri = appendTokenToRedirectUri(redirectUri, jwtToken);
        log.info("Redirecting to: {}", finalRedirectUri);
        response.sendRedirect(finalRedirectUri);
    }

    /**
     * Appends the JWT token to the given redirect URI as a query parameter.
     * <p>
     * If the URI already contains query parameters, appends using "&".
     * Otherwise, starts the query string with "?".
     * </p>
     *
     * @param redirectUri The URI to which the user will be redirected.
     * @param jwtToken    The JWT token to add as a parameter.
     * @return The new URI with the token as a query parameter.
     */
    private String appendTokenToRedirectUri(String redirectUri, String jwtToken) {
        return redirectUri.contains("?")
                ? redirectUri + "&token=" + jwtToken
                : redirectUri + "?token=" + jwtToken;
    }

    /**
     * Creates a new user in the database using information from an OAuth2 authentication.
     * <p>
     * This is used as a fallback when a user logs in with Google for the first time.
     * Generates a random password, sets the provider, and assigns the "User" role.
     * </p>
     *
     * @param oauth2User The OAuth2User containing the user's Google profile information.
     * @return The newly created and persisted User entity.
     */
    private User createUserFromOAuth2(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String imageUrl = oauth2User.getAttribute("picture");

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

        return userRepository.saveAndFlush(newUser);
    }
}
