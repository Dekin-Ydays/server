package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityRequest {

    @NotBlank(message = "The city name cannot be empty.")
    @Size(min = 2, message = "The name must be between 2 and 100 characters.")
    private String name;
    @Size(max = 500, message = "The description cannot exceed 500 characters.")
    private String description;
    private String region;
    private String postalCode;
    private String country;
    @NotNull(message = "Latitude is required.")
    private Double latitude;
    @NotNull(message = "Longitude is required.")
    private Double longitude;
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "The banner URL must be a valid URL format.")
    private String bannerUrl;
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "The image URL must be a valid URL format.")
    private String imageUrl;
    private String content;

    @Positive(message = "Cities IDs must be positive numbers.")
    private List<Long> nearestCityIds;
}