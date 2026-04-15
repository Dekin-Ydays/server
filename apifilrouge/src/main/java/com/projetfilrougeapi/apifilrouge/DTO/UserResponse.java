package com.projetfilrougeapi.apifilrouge.DTO;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
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
 // private List<SocialLink> socials;
 // private List<CategorySummary> categories;
    private Double note;

    public static UserResponse fromEntity(User user) {


        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .pseudo(user.getPseudo())
                .slug(user.getSlug())
                .email(user.getEmail())
                .phone(user.getPhone())
                .eventPastCount(1)
                .eventsCount(0)
                .role(user.getRole())
                .description(user.getDescription())
                .imageUrl(user.getImageUrl())
                .bannerUrl(user.getBannerUrl())
                .note(user.getNote())
                .build();
    }


}