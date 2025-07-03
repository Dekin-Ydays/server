package com.projetfilrougeapi.apifilrouge.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtre d'authentification JWT qui intercepte chaque requête HTTP.
 * <p>
 * Ce filtre vérifie la présence et la validité des tokens JWT dans l'en-tête "Authorization"
 * des requêtes entrantes. Si un token valide est trouvé, l'utilisateur correspondant est
 * authentifié et ses informations sont placées dans le contexte de sécurité Spring pour
 * la durée de la requête.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Intercepte et traite chaque requête HTTP pour vérifier l'authentification JWT.
     * <p>
     * Cette méthode extrait le token JWT de l'en-tête "Authorization", vérifie sa validité,
     * et authentifie l'utilisateur si le token est valide. Si aucun token n'est présent ou
     * si le token est invalide, la requête continue sans authentification.
     * </p>
     *
     * @param request     La requête HTTP entrante
     * @param response    La réponse HTTP sortante
     * @param filterChain La chaîne de filtres pour continuer le traitement
     * @throws ServletException Si une erreur survient pendant le traitement de la requête
     * @throws IOException      Si une erreur d'entrée/sortie survient
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String Usermail;
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authorizationHeader.substring(7);
        Usermail = jwtService.extractUsername(jwt);
        if (Usermail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(Usermail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                System.out.println("Autorités de l'utilisateur : " + userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }
        filterChain.doFilter(request, response);
    }
}
