package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String pseudo;

    private Role role;
    private Boolean isOrganizer;
    private String phone;
    private String description;
    private String imageUrl;
    private String bannerUrl;
    private Double note;
    private List<Long> categoryIds;
    private String socials;
}
