package com.projetfilrougeapi.apifilrouge.auth;

/*import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;*/
import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
/**
 * Contrôleur REST qui gère les opérations d'authentification et d'enregistrement des utilisateurs.
 * <p>
 * Ce contrôleur expose des endpoints pour l'enregistrement de nouveaux utilisateurs et l'authentification
 * des utilisateurs existants via des requêtes HTTP POST.
 * </p>
 *
 */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthenticationController {
    

    private final AuthenticationService service;
    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param request Les informations de l'utilisateur à enregistrer
     * @return Une réponse HTTP contenant le token d'authentification et autres informations pertinentes
     */

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserRequest request) throws Exception {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Authentifie un utilisateur existant.
     *
     * @param request Les informations d'authentification (identifiant et mot de passe)
     * @return Une réponse HTTP contenant le token d'authentification et autres informations pertinentes
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}