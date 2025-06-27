package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.config.oauth.CustomOAuth2UserService;
import com.projetfilrougeapi.apifilrouge.config.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
/**
 * Configuration de sécurité de l'application.
 * <p>
 * Cette classe définit les règles d'autorisation et d'authentification pour l'application.
 * Elle configure les accès aux différentes API en fonction des rôles utilisateurs,
 * gère l'authentification par JWT et OAuth2, et paramètre les règles CORS.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    /**
     * Liste des URL accessibles sans authentification.
     */
    private static final String[] WHITE_LIST_URL = {
            "/api/v1/places/**",
            "/api/v1/cities/**",
            "/api/v1/events/**",
            "/api/v1/categories/**",
            "/api/v1/users/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
    };
    /**
     * Configure la source de configuration CORS.
     * <p>
     * Définit les origines, méthodes et en-têtes autorisés pour les requêtes
     * cross-origin, ainsi que la durée de mise en cache des pré-vérifications.
     * </p>
     *
     * @return La source de configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    /**
     * Configure la chaîne de filtres de sécurité.
     * <p>
     * Cette méthode définit l'ensemble des règles de sécurité de l'application :
     * - Désactive CSRF
     * - Configure CORS
     * - Définit les règles d'autorisation pour chaque endpoint API selon les rôles
     * - Configure la gestion des sessions (stateless)
     * - Ajoute les filtres d'authentification JWT
     * - Configure l'authentification OAuth2
     * </p>
     *
     * @param http La configuration HTTP à modifier
     * @return La chaîne de filtres de sécurité configurée
     * @throws Exception Si une erreur survient pendant la configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/error").permitAll()
                            .requestMatchers(HttpMethod.GET, WHITE_LIST_URL).permitAll()

                            .requestMatchers(HttpMethod.POST, "/api/v1/places").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/cities").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/events/*").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/events/*/participant").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/events/*/participants").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/invitations").hasAnyRole("Admin", "Organizer", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").hasAnyRole("Admin", "AuthService", "User") //Tickets
                            .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyRole("Admin", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/api/v1/reports").hasAnyRole("Admin", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/api/v1/users").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/api/v1/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")


                            .requestMatchers(HttpMethod.GET, "/api/v1/invitations/**").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("Admin", "AuthService") //tickets mais faut pas récup les tikets d'une autre personne
                            .requestMatchers(HttpMethod.GET, "/api/v1/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/api/v1/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")


                            .requestMatchers(HttpMethod.PATCH, "/api/v1/places").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/cities").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/**").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/users").hasAnyRole("Admin", "AuthService")

                            .requestMatchers(HttpMethod.DELETE, "/api/v1/places").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/cities").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/invitations").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/orders").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/users").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .build();
    }
}
