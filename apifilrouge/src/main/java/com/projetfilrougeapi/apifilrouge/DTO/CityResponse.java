package com.projetfilrougeapi.apifilrouge.DTO;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {
    private Long id;
    private String name;
    private Location location;
    private String region;
    private String postalCode;
    private String country;
    private String bannerUrl;
    private int eventsCount;
    private long eventsPastCount;
    private String imageUrl;
    private String content;
    private List<Long> nearestCities;

    public static CityResponse fromEntity(City city) {
        long pastEvents = city.getEvents() != null ?
                city.getEvents().stream().filter(e -> e.getDate().isBefore(LocalDateTime.now())).count() : 0;

        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .location(new Location(city.getLatitude(), city.getLongitude()))
                .region(city.getRegion())
                .postalCode(city.getPostalCode())
                .country(city.getCountry())
                .bannerUrl(city.getBannerUrl())
                .eventsCount(city.getEvents() != null ? city.getEvents().size() : 0)
                .eventsPastCount(pastEvents)
                .imageUrl(city.getImageUrl())
                .content(city.getContent())
                .nearestCities(city.getNearestCities().stream().map(City::getId).collect(Collectors.toList()))
                .build();
    }
}
