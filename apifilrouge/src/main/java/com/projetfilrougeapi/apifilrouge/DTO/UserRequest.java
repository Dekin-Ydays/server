package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {
    @NotBlank(message = "First name is required.")
    @Size(max = 255, message = "First name must not exceed 255 characters.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    @Size(max = 255, message = "Last name must not exceed 255 characters.")
    private String lastName;
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;
    @NotBlank(message = "Pseudo is required.")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-\\.]+$", message = "Pseudo can only contain letters, numbers, dashes, underscores, and dots.")
    private String pseudo;

    @NotNull(message = "Role is required.")
    private Role role;

    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Phone must be a valid international format or between 8 to 15 digits.")
    private String phone;
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;
    @Pattern(regexp = "^(http|https)://.*", message = "Image URL must be a valid URL starting with http or https.")
    private String imageUrl;
    @Pattern(regexp = "^(http|https)://.*", message = "Banner URL must be a valid URL starting with http or https.")
    private String bannerUrl;
    @DecimalMin(value = "0.0", message = "The note cannot be less than 0.")
    @DecimalMax(value = "5.0", message = "The note cannot exceed 5.")
    private Double note;
    private List<String> categoryKeys;
    private String socials;
}
