package com.projetfilrougeapi.apifilrouge.config.oauth;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Loads the user from the OAuth2 provider, then processes them in our local database.
     * This method is the core of the OAuth2 login flow. It's responsible for:
     * Checking if the user already exists in our system based on their email.
     * If the user exists, it updates their profile info and performs a critical security check to prevent account takeover.
     * If the user does not exist, it creates a new user account in our database with a secure, unusable password.
     *
     * @param userRequest The request containing user information from the OAuth2 provider.
     * @return The OAuth2User object to be used by Spring Security for establishing the session.
     * @throws OAuth2AuthenticationException if the user cannot be authenticated or if a security policy is violated.
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            // Fetch user details from the OAuth2 provider
            OAuth2User oauth2User = super.loadUser(userRequest);

            String email = oauth2User.getAttribute("email");
            String firstName = oauth2User.getAttribute("given_name");
            String lastName = oauth2User.getAttribute("family_name");
            String imageUrl = oauth2User.getAttribute("picture");

            // Check if a user with this email already exists in our database
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                // The user already exists
                User existingUser = userOptional.get();

                // Prevent account takeover of a local account
                if (existingUser.getProvider() != AuthProvider.GOOGLE) {
                    OAuth2Error error = new OAuth2Error("account_exists", "An account with this email already exists. Please log in with your password.", null);
                    throw new OAuth2AuthenticationException(error, error.getDescription());
                }

                // If the user already signed up with Google, just update their profile information
                existingUser.setFirstName(firstName);
                existingUser.setLastName(lastName);
                existingUser.setImageUrl(imageUrl);
                userRepository.saveAndFlush(existingUser);

            } else {
                // The user does not exist, so we create a new one
                User newUser = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .pseudo(email)
                        .imageUrl(imageUrl)
                        .role(Role.User)
                        .provider(AuthProvider.GOOGLE)
                        // Generate a secure, random password
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .build();
                userRepository.saveAndFlush(newUser);
            }

            // Return the OAuth2User object for Spring Security to continue the process
            return oauth2User;

        } catch (OAuth2AuthenticationException e) {
            // If we threw this exception ourselves for security reasons, let it pass through.
            throw e;
        } catch (DataAccessException e) {
            // Catch specific database-related errors
            OAuth2Error error = new OAuth2Error("database_error", "Database error while processing the OAuth2 profile.", null);
            throw new OAuth2AuthenticationException(error, e.getMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected errors
            OAuth2Error error = new OAuth2Error("unexpected_error", "An unexpected error occurred during OAuth2 authentication.", null);
            throw new OAuth2AuthenticationException(error, e.getMessage(), e);
        }
    }
}