package com.projetfilrougeapi.apifilrouge.auth;

import com.github.slugify.Slugify;
import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import com.projetfilrougeapi.apifilrouge.config.JwtService;
import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service qui gère les opérations d'authentification des utilisateurs.
 * <p>
 * Ce service fournit des méthodes pour l'enregistrement de nouveaux utilisateurs et l'authentification
 * des utilisateurs existants. Il s'occupe également de la génération des tokens JWT pour les
 * sessions authentifiées.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;
    private final CategoryRepository categoryRepository;
    private final Slugify slugify = Slugify.builder().build();
    private final UserRepository userRepository;

    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param request Les informations du nouvel utilisateur à enregistrer
     * @return Une réponse contenant le token JWT généré pour l'utilisateur enregistré
     */
    public AuthenticationResponse register(UserRequest request) throws Exception {
        userRepository.findByEmail(request.getEmail()).ifPresent(
                user -> {
                    throw new RuntimeException("Email already exists");
                }
        );
        List<Category> categories = new ArrayList<>();
        if (request.getCategoryKeys() != null) {
            categories = categoryRepository.findByKeyIn(request.getCategoryKeys());
        }
        String generatedSlug = slugify.slugify(request.getPseudo());

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .description(request.getDescription())
                .provider(AuthProvider.LOCAL)
                .pseudo(request.getPseudo())
                .slug(generatedSlug)
                .password(encoder.encode(request.getPassword()))
                .role(Role.User)
                .categories(categories)
                .totalReviews(0)
                .countReportsReceived(0)
                .isBanned(false)
                .build();

        emailSender.sendWelcomeEmail(user);
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    /**
     * Authentifie un utilisateur existant.
     *
     * @param request Les informations d'authentification (email et mot de passe)
     * @return Une réponse contenant le token JWT généré pour l'utilisateur authentifié
     * @throws org.springframework.security.core.AuthenticationException Si l'authentification échoue
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Retrieve the user by email
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If the user's role is Banned, block the authentication
        if (user.getRole() == Role.Banned) {
            throw new RuntimeException("Your account has been banned. Please contact the administrator.");
        }

        // Authenticate user's credentials (email and password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        // Generate the JWT token for the authenticated user
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}