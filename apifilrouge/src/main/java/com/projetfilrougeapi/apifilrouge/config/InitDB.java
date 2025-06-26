package com.projetfilrougeapi.apifilrouge.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class InitDB {
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CityRepository cityRepository;

    public InitDB(PlaceRepository placeRepository, CategoryRepository categoryRepository, UserRepository userRepository, EventRepository eventRepository, CityRepository cityRepository) {
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.cityRepository = cityRepository;
    }

/*    @Bean
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

    }*/

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
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.User)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Admin")
                                .lastName("System")
                                .email("admin@example.com")
                                .pseudo("admin")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Admin)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Auth")
                                .lastName("Service")
                                .email("auth@example.com")
                                .pseudo("auth")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.AuthService)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Event")
                                .lastName("Organizer")
                                .email("organizer@example.com")
                                .pseudo("organizer")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("password"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Organizer)
                                .build()
                );
                userRepository.saveAll(users);
            }
        };
    }

//    @Bean
//    CommandLineRunner initEvent(EventRepository eventRepository,
//                                UserRepository userRepository,
//                                CategoryRepository categoryRepository) {
//        return args -> {
//            if (eventRepository.count() == 0) {
//                try {
//                    // Charger le fichier JSON depuis les ressources
//                    InputStream inputStream = getClass().getResourceAsStream("/json/events.json");
//                    if (inputStream == null) {
//                        System.err.println("Impossible de trouver le fichier events.json dans les ressources");
//                        return;
//                    }
//
//                    // Récupérer toutes les catégories et utilisateurs existants
//                    List<Category> categories = categoryRepository.findAll();
//                    List<User> users = userRepository.findAll();
//
//                    // Créer un parser JSON avec support pour les dates Java 8
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    objectMapper.registerModule(new JavaTimeModule()); // Ajout du module JavaTimeModule
//
//                    // Obtenir le JsonNode racine (tableau d'événements)
//                    JsonNode rootNode = objectMapper.readTree(inputStream);
//                    inputStream.close();
//
//                    // Liste pour stocker les événements créés
//                    List<Event> events = new ArrayList<>();
//
//                    // Parcourir chaque événement dans le fichier JSON
//                    for (JsonNode eventNode : rootNode) {
//                        // Créer un nouvel événement à partir des données JSON
//                        Event event = new Event();
//
//                        // Définir les propriétés de base
//                        event.setName(eventNode.get("name").asText());
//                        event.setDescription(eventNode.get("description").asText());
//                        event.setAddress(eventNode.get("address").asText());
//                        event.setMaxCustomers(eventNode.get("maxCustomers").asInt());
//                        event.setIsTrending(eventNode.get("isTrending").asBoolean());
//                        event.setIsFirstEdition(eventNode.get("isFirstEdition").asBoolean());
//                        event.setStatus(EventStatus.valueOf(eventNode.get("status").asText()));
//                        event.setPrice(eventNode.get("price").asDouble());
//                        event.setContentHtml(eventNode.has("contentHtml") ? eventNode.get("contentHtml").asText() : null);
//                        event.setImageUrl(eventNode.has("imageUrl") ? eventNode.get("imageUrl").asText() : null);
//
//                        // Traiter la date
//                        String dateStr = eventNode.get("date").asText();
//                        event.setDate(LocalDateTime.parse(dateStr));
//
//                        // Associer la place et la ville
//                        Long placeId = eventNode.get("placeId").asLong();
//                        Long cityId = eventNode.get("cityId").asLong();
//                        event.setPlace(placeRepository.findById(placeId).orElse(null));
//                        event.setCity(cityRepository.findById(cityId).orElse(null));
//
//                        // Associer l'organisateur (premier utilisateur pour cet exemple)
//                        if (!users.isEmpty()) {
//                            event.setOrganizer(users.get(0));
//                        }
//
//                        // Traiter les catégories
//                        if (eventNode.has("categoryKeys")) {
//                            Set<Category> eventCategories = new HashSet<>();
//                            JsonNode categoryKeysNode = eventNode.get("categoryKeys");
//                            List<String> categoryKeys = new ArrayList<>();
//                            for (JsonNode keyNode : categoryKeysNode) {
//                                categoryKeys.add(keyNode.asText());
//                            }
//                            // Trouver les catégories correspondantes
//                            if (!categoryKeys.isEmpty()) {
//                                List<Category> matchingCategories = categoryRepository.findByKeyIn(categoryKeys);
//                                eventCategories.addAll(matchingCategories);
//                            }
//                            event.setCategories(eventCategories);
//                        }
//
//                        // Traiter les participants
//                        if (eventNode.has("participantIds")) {
//                            List<User> participants = new ArrayList<>();
//                            JsonNode participantIdsNode = eventNode.get("participantIds");
//                            for (JsonNode idNode : participantIdsNode) {
//                                Long participantId = idNode.asLong();
//                                users.stream()
//                                    .filter(user -> user.getId().equals(participantId))
//                                    .findFirst()
//                                    .ifPresent(participants::add);
//                            }
//                            event.setParticipants(participants);
//                        }
//
//                        events.add(event);
//                    }
//
//                    // Sauvegarder tous les événements
//                    eventRepository.saveAll(events);
//                    System.out.println("Événements chargés avec succès : " + events.size() + " événements");
//
//                } catch (Exception e) {
//                    System.err.println("Erreur lors du chargement des événements : " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        };
//    }

}