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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private static final String[] WHITE_LIST_URL = {
            "/places/**",
            "/cities/**",
            "/events/**",
            "/categories",
            "/users/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
    };

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

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

                            .requestMatchers(HttpMethod.POST, "/places").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/cities").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/events/*").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/events/*/participant").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/events/*/participants").hasAnyRole("Admin", "Organizer", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/invitations").hasAnyRole("Admin", "Organizer", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/orders/**").hasAnyRole("Admin", "AuthService", "User") //Tickets
                            .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("Admin", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/reports").hasAnyRole("Admin", "AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.POST, "/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")


                            .requestMatchers(HttpMethod.GET, "/invitations/**").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.GET, "/categories/**").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("Admin", "AuthService") //tickets mais faut pas récup les tikets d'une autre personne
                            .requestMatchers(HttpMethod.GET, "/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.GET, "/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")


                            .requestMatchers(HttpMethod.PATCH, "/places").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/cities").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/orders/**").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/users").hasAnyRole("Admin", "AuthService")

                            .requestMatchers(HttpMethod.DELETE, "/places").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/cities").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/events").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/invitations").hasAnyRole("Admin", "AuthService", "Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/categories").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/orders").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/reports").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/users").hasAnyRole("Admin", "AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/reviews").hasAnyRole("Admin", "AuthService", "User", "Organizer")
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
