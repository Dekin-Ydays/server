package com.projetfilrougeapi.apifilrouge.config.oauth;

import com.github.slugify.Slugify;
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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles successful OAuth2 authentication by managing user provisioning and JWT generation.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Validating or creating user records after OAuth2 login</li>
 *   <li>Generating a JWT for authenticated users</li>
 *   <li>Validating redirect URIs for security</li>
 *   <li>Redirecting users to frontend applications with their JWT token</li>
 *   <li>Refusing login for banned or unauthorized users</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> allowedRedirectUris;
    private final Slugify slugify = Slugify.builder().build();

    /**
     * Creates an instance with injected dependencies.
     *
     * @param userRepository  The user repository for database access.
     * @param jwtService      The JWT service for token generation.
     * @param passwordEncoder The password encoder for generating secure passwords.
     * @param allowedUris     Comma-separated list of allowed frontend redirect URIs.
     */
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
     * Called by Spring Security after successful OAuth2 authentication.
     * <p>
     * Validates the authenticated user's existence or creates a new account,
     * ensures the account is not banned, generates a JWT token, and redirects
     * the user to an authorized frontend URI with their token.
     *
     * @param request        The current HTTP request.
     * @param response       The HTTP response.
     * @param authentication The authenticated principal.
     * @throws IOException      If an I/O error occurs during redirection.
     * @throws ServletException On servlet errors.
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        log.info("OAuth2 authentication successful for email: {}", email);

        // Verify if the user already exist
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();

            // 2. Refuse if it is not an account of Google
            if (user.getProvider() != AuthProvider.GOOGLE) {
                log.error("Tentative de login Google sur un compte local: {}", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Un compte avec cet email existe déjà. Merci de vous connecter avec votre mot de passe.");
                return;
            }

            // 3. Refuse if the user is banned
            if (user.getRole() == Role.Banned) {
                log.warn("Login refusé (banni) pour: {}", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your account has been banned.");
                return;
            }

            // Update information
            String firstName = oauthUser.getAttribute("given_name");
            String lastName = oauthUser.getAttribute("family_name");
            String imageUrl = oauthUser.getAttribute("picture");
            boolean needUpdate = false;

            if (firstName != null && !firstName.equals(user.getFirstName())) {
                user.setFirstName(firstName);
                needUpdate = true;
            }
            if (lastName != null && !lastName.equals(user.getLastName())) {
                user.setLastName(lastName);
                needUpdate = true;
            }
            if (imageUrl != null && !imageUrl.equals(user.getImageUrl())) {
                user.setImageUrl(imageUrl);
                needUpdate = true;
            }
            if (needUpdate) {
                userRepository.saveAndFlush(user);
            }
        } else {
            // 5. Create a new Google user
            user = createUserFromOAuth2(oauthUser);
            log.info("New user created via Google: {}", user.getEmail());
        }

        String jwtToken = jwtService.generateToken(user);
        log.info("JWT token generated for: {}", user.getEmail());

        String redirectUri = (String) request.getSession().getAttribute("oauth2_redirect_uri");
        if (redirectUri == null || redirectUri.isBlank()) {
            log.error("No redirect_uri in session! Rejecting for safety.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid redirect_uri.");
            return;
        }
        log.info("Found redirect_uri from session: {}", redirectUri);

        boolean allowed = allowedRedirectUris.stream().anyMatch(redirectUri::startsWith);
        if (!allowed) {
            log.error("Redirection not allowed: {}", redirectUri);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Redirection URL non autorisée !");
            return;
        }

        request.getSession().removeAttribute("oauth2_redirect_uri");
        String finalRedirectUri = appendTokenToRedirectUri(redirectUri, jwtToken);
        log.info("Redirecting to: {}", finalRedirectUri);
        response.sendRedirect(finalRedirectUri);
    }

    /**
     * Adds the JWT token as a query parameter to the redirect URI.
     * <p>
     * If the URI already contains query parameters, the token is appended using '&'.
     * Otherwise, it starts the query string with '?'.
     *
     * @param redirectUri The destination URI.
     * @param jwtToken    The JWT token to append.
     * @return The URI with the JWT token as a query parameter.
     */
    private String appendTokenToRedirectUri(String redirectUri, String jwtToken) {
        return redirectUri.contains("?")
                ? redirectUri + "&token=" + jwtToken
                : redirectUri + "?token=" + jwtToken;
    }

    /**
     * Creates and persists a new user account using information from the OAuth2 provider.
     * <p>
     * Sets the user's email, name, slug, role, provider, and a randomly generated password.
     *
     * @param oauth2User The OAuth2 user information.
     * @return The newly created User entity.
     * @throws OAuth2AuthenticationException If the account cannot be created.
     */
    private User createUserFromOAuth2(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String imageUrl = oauth2User.getAttribute("picture");
        String pseudo = (firstName + " " + lastName).trim();
        if (pseudo.isBlank()) pseudo = email;
        String generatedSlug = slugify.slugify(pseudo);

        User newUser = User.builder()
                .firstName(firstName != null ? firstName : "Unknown")
                .lastName(lastName != null ? lastName : "User")
                .email(email)
                .pseudo(pseudo)
                .slug(generatedSlug)
                .imageUrl(imageUrl)
                .role(Role.User)
                .provider(AuthProvider.GOOGLE)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();

        return userRepository.saveAndFlush(newUser);
    }
}
