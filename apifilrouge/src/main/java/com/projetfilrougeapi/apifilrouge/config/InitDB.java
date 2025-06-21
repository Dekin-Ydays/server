package com.projetfilrougeapi.apifilrouge.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;

@Configuration
public class InitDB {

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        Category.builder()
                                .name("Sport")
                                .description("Événements sportifs et activités physiques")
                                .key("sport")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Culture")
                                .description("Art, musique, théâtre et expositions")
                                .key("culture")
                                .isTrending(true)
                                .build(),
                        Category.builder()
                                .name("Technologie")
                                .description("Conférences tech, meetups et hackathons")
                                .key("technology")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Nourriture")
                                .description("Événements culinaires et dégustations")
                                .key("food")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Bien-être")
                                .description("Yoga, méditation et activités de bien-être")
                                .key("wellness")
                                .isTrending(false)
                                .build()
                );
                categoryRepository.saveAll(categories);
            }
        };

    }
    @Bean
    CommandLineRunner initUsers ( UserRepository userRepository,PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.user.User> users = List.of(
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("User")
                                .lastName("Normal")
                                .email("user@example.com")
                                .pseudo("user")
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.User)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Admin")
                                .lastName("System")
                                .email("admin@example.com")
                                .pseudo("admin")
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Admin)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Auth")
                                .lastName("Service")
                                .email("auth@example.com")
                                .pseudo("auth")
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.AuthService)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Event")
                                .lastName("Organizer")
                                .email("organizer@example.com")
                                .pseudo("organizer")
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Organizer)
                                .build()
                );
                userRepository.saveAll(users);
            }
        };
    }

    /*@Bean
    CommandLineRunner initEvent (EventRepository eventRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                try {
                    // Charger le fichier events.json depuis les ressources
                    ObjectMapper objectMapper = new ObjectMapper();
                    InputStream inputStream = getClass().getResourceAsStream("/json/events.json");
                    if (inputStream == null) {
                        System.err.println("Fichier events.json non trouvé dans les ressources");
                        return;
                    }

                    // Convertir le JSON en liste d'événements
                    List<Event> events = objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<Event>>() {}
                    );

                    // Sauvegarder tous les événements
                    eventRepository.saveAll(events);
                    System.out.println(events.size() + " événements chargés avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement des événements: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }*/

}
