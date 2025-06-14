package com.projetfilrougeapi.apifilrouge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/places",
            "/cities",
            "/events"};

    private final JwtAuthFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/error").permitAll()
                            .requestMatchers(HttpMethod.GET, WHITE_LIST_URL).permitAll()

                            .requestMatchers(HttpMethod.POST, "/places").hasAnyRole("Admin", "Organizer","AuthService")
                            .requestMatchers(HttpMethod.POST, "/cities").hasAnyRole("Admin", "Organizer","AuthService")
                            .requestMatchers(HttpMethod.POST, "/events/*").hasAnyRole("Admin", "Organizer","AuthService")
                            .requestMatchers(HttpMethod.POST, "/invitations").hasAnyRole("Admin", "Organizer","AuthService", "User")
                            .requestMatchers(HttpMethod.POST, "/categories").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.POST, "/orders/**").hasAnyRole("Admin","AuthService","User") //Tickets
                            .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("Admin","AuthService","User")
                            .requestMatchers(HttpMethod.POST, "/reports").hasAnyRole("Admin","AuthService","User")
                            .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("Admin","AuthService")


                            .requestMatchers(HttpMethod.GET, "/places/**").hasAnyRole("Admin","AuthService","User","Organizer")
                            .requestMatchers(HttpMethod.GET, "/cities/**").hasAnyRole("Admin","AuthService","User","Organizer")
                            .requestMatchers(HttpMethod.GET, "/events/**").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.GET, "/invitations/**").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.GET, "/categories/**").hasAnyRole("Admin","AuthService","Organizer", "User")
                            .requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("Admin","AuthService","User")
                            .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("Admin","AuthService","User")
                            .requestMatchers(HttpMethod.GET, "/reports").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("Admin","AuthService")

                            .requestMatchers(HttpMethod.PATCH, "/places").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/cities").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/categories").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/orders/**").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/reports").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/users").hasAnyRole("Admin","AuthService")

                            .requestMatchers(HttpMethod.DELETE, "/places").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/cities").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/events").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/invitations").hasAnyRole("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/categories").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/orders").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/reports").hasAnyRole("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/users").hasAnyRole("Admin","AuthService")


                            .anyRequest().authenticated();
                })
                .oauth2Login(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}