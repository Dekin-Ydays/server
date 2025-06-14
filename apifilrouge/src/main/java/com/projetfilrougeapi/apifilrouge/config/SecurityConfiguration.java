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

import static com.projetfilrougeapi.apifilrouge.endpoint_api.user.Permission.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/places/*",
            "/cities/*",
            "/events/*"};

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

                           /* .requestMatchers(HttpMethod.POST, "/places").hasAnyAuthority(ADMIN_CREATE.name(), ORGANIZER_CREATE.name(), AUTH_SERVICE_CREATE.name())
                            *///.requestMatchers(HttpMethod.POST, "/cities").hasAnyAuthority(ADMIN_CREATE.name(), ORGANIZER_CREATE.name(),AUTH_SERVICE_CREATE.name())
                            /*.requestMatchers(HttpMethod.POST, "/events/*").hasAnyAuthority(ADMIN_CREATE.name(), ORGANIZER_CREATE.name(),AUTH_SERVICE_CREATE.name())
                            .requestMatchers(HttpMethod.POST, "/invitations").hasAnyAuthority(ADMIN_CREATE.name(), ORGANIZER_CREATE.name(),AUTH_SERVICE_CREATE.name(), USER_CREATE.name())
                            .requestMatchers(HttpMethod.POST, "/categories").hasAnyAuthority(ADMIN_CREATE.name(),AUTH_SERVICE_CREATE.name())
                            .requestMatchers(HttpMethod.POST, "/orders/**").hasAnyAuthority(ADMIN_CREATE.name(),AUTH_SERVICE_CREATE.name(),USER_CREATE.name()) //Tickets
                            .requestMatchers(HttpMethod.POST, "/orders").hasAnyAuthority(ADMIN_CREATE.name(),AUTH_SERVICE_CREATE.name(),USER_CREATE.name())
                            .requestMatchers(HttpMethod.POST, "/reports").hasAnyAuthority(ADMIN_CREATE.name(),AUTH_SERVICE_CREATE.name(),USER_CREATE.name())
                            .requestMatchers(HttpMethod.POST, "/users").hasAnyAuthority(ADMIN_CREATE.name(),AUTH_SERVICE_CREATE.name())


                            .requestMatchers(HttpMethod.GET, "/places/**").hasAnyAuthority(ADMIN_READ.name(), AUTH_SERVICE_READ.name(), USER_READ.getPermission(),"ORGANIZER_READ")
                            .requestMatchers(HttpMethod.GET, "/cities/**").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name(),"USER_READ","ORGANIZER_READ")
                            .requestMatchers(HttpMethod.GET, "/events/**").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name(),"ORGANIZER_READ")
                            .requestMatchers(HttpMethod.GET, "/invitations/**").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name(),"ORGANIZER_READ")
                            .requestMatchers(HttpMethod.GET, "/categories/**").hasAnyAuthority(ADMIN_READ.getPermission(),AUTH_SERVICE_READ.name(),"USER_READ","ORGANIZER_READ")
                            .requestMatchers(HttpMethod.GET, "/orders").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name(),"USER_READ")
                            .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name(),"USER_READ")
                            .requestMatchers(HttpMethod.GET, "/reports").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name())
                            .requestMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority(ADMIN_READ.name(),AUTH_SERVICE_READ.name())

                            .requestMatchers(HttpMethod.PATCH, "/places").hasAnyAuthority("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/cities").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyAuthority("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/events").hasAnyAuthority("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.PATCH, "/categories").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/orders/**").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/reports").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.PATCH, "/users").hasAnyAuthority("Admin","AuthService")

                            .requestMatchers(HttpMethod.DELETE, "/places").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/cities").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/events").hasAnyAuthority("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/invitations").hasAnyAuthority("Admin","AuthService","Organizer")
                            .requestMatchers(HttpMethod.DELETE, "/categories").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/orders").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/reports").hasAnyAuthority("Admin","AuthService")
                            .requestMatchers(HttpMethod.DELETE, "/users").hasAnyAuthority("Admin","AuthService")*/


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