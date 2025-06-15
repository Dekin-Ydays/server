package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityRequest {
    private String name;
    private String description;
    private String region;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String bannerUrl;
    private String imageUrl;
    private String content;
    private List<Long> nearestCityIds;
}