package com.projetfilrougeapi.apifilrouge.DTO;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String lastName;
    private String firstName;
    private String pseudo;
    private String slug;
    private String email;
    private String phone;
    private long eventPastCount;
    private int eventsCount;
    private Role role;
    private String description;
    private String imageUrl;
    private String bannerUrl;
    private List<SocialLink> socials;
    private List<CategorySummary> categories;
    private Double note;

    public static UserResponse fromEntity(User user) {

        // Calcule le nombre d'événements passés organisés par l'utilisateur
        long pastEvents = (user.getEvents() != null) ? user.getEvents().stream().filter(event -> event.getDate().isBefore(LocalDateTime.now())).count() : 0;
        List<CategorySummary> categories = user.getCategories().stream().map(category -> new CategorySummary(category.getName(), category.getKey())).collect(Collectors.toList());
        // Parse la chaîne JSON des réseaux sociaux
        List<SocialLink> socialLinks = parseSocials(user.getSocials());

        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .pseudo(user.getPseudo())
                .slug(user.getSlug())
                .email(user.getEmail())
                .phone(user.getPhone())
                .eventPastCount(pastEvents)
                .eventsCount(user.getEvents() != null ? user.getEvents().size() : 0)
                .role(user.getRole())
                .description(user.getDescription())
                .imageUrl(user.getImageUrl())
                .bannerUrl(user.getBannerUrl())
                .socials(socialLinks)
                .categories(categories)
                .note(user.getNote())
                .build();
    }

    /**
     * Parses the JSON string of social network links into a list of SocialLink objects.
     *
     * @param socialsJson The JSON string representing social network links.
     * @return A list of SocialLink objects parsed from the JSON string,
     *         or an empty list if the input is null, empty, or invalid JSON.
     */
    private static List<SocialLink> parseSocials(String socialsJson) {
        if (socialsJson == null || socialsJson.trim().isEmpty()) {
            return Collections.emptyList();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(socialsJson, new TypeReference<List<SocialLink>>() {});
        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error for social links: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}