package com.projetfilrougeapi.apifilrouge.auth;

import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import com.projetfilrougeapi.apifilrouge.config.JwtService;
import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param request Les informations du nouvel utilisateur à enregistrer
     * @return Une réponse contenant le token JWT généré pour l'utilisateur enregistré
     */
    public AuthenticationResponse register(UserRequest request) throws Exception {

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .provider(AuthProvider.LOCAL)
                .pseudo(request.getPseudo())
                .password(encoder.encode(request.getPassword()))
                .role(Role.User)
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}