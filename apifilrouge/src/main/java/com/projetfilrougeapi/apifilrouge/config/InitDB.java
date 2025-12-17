package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Configuration pour l'initialisation de la base de données.
 * <p>
 * Cette classe est responsable de l'initialisation des données par défaut dans la base de données
 * au démarrage de l'application. Elle définit des beans CommandLineRunner qui s'exécutent
 * lors du lancement de l'application pour peupler les tables avec des données initiales.
 * </p>
 */
@Configuration
public class InitDB {

    /**
     * Initialise des utilisateurs par défaut dans la base de données si aucun n'existe déjà.
     * <p>
     * Ce bean crée quatre utilisateurs avec différents rôles (User, Admin, AuthService, Organizer)
     * et les enregistre dans la base de données uniquement si celle-ci est vide.
     * Les mots de passe sont encodés avant d'être stockés.
     * </p>
     *
     * @param userRepository  Le repository pour accéder aux données des utilisateurs
     * @param passwordEncoder L'encodeur utilisé pour sécuriser les mots de passe
     * @return Un CommandLineRunner qui initialise les utilisateurs
     */
    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.user.User> users = List.of(
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("User")
                                .lastName("Normal")
                                .email("user@example.com")
                                .pseudo("user")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.User)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Admin")
                                .lastName("System")
                                .email("admin@example.com")
                                .pseudo("admin")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Admin)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Auth")
                                .lastName("Service")
                                .email("auth@example.com")
                                .pseudo("auth")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.AuthService)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Event")
                                .lastName("Organizer")
                                .email("organizer@example.com")
                                .pseudo("organizer")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Organizer)
                                .build()
                );
                userRepository.saveAll(users);
            }
        };
    }

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
               List<com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category> categories = List.of(
                          com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                  .categoryName("Rock")
                                  .categoryDescription("Rock music category")
                                  .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Rock)
                                  .build(),
                          com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                  .categoryName("Kpop")
                                  .categoryDescription("Kpop music category")
                                  .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Kpop)
                                  .build(),
                          com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                  .categoryName("Jazz")
                                  .categoryDescription("Jazz music category")
                                  .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Jazz)
                                  .build(),
                          com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                  .categoryName("HipHop")
                                  .categoryDescription("HipHop music category")
                                  .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.HipHop)
                                  .build()
               );
               categoryRepository.saveAll(categories);
            }
        };
    }

}
