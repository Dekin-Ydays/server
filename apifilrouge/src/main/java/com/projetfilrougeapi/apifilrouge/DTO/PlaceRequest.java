package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequest {

    @NotBlank(message = "The place name cannot be empty.")
    @Size(min = 2, max = 150, message = "The name must be between 2 and 150 characters.")
    private String name;

    @Size(max = 500, message = "The description cannot exceed 500 characters.")
    private String description;

    @NotBlank(message = "The address cannot be empty.")
    private String address;

    private String type;

    @NotNull(message = "Latitude is required.")
    private Double latitude;

    @NotNull(message = "Longitude is required.")
    private Double longitude;

    private String cityName;

    private String bannerUrl;

    private String imageUrl;

    private String content;

    @NotNull(message = "A city ID is required.")
    @Positive(message = "City ID must be a positive number.")
    private Long cityId;
}