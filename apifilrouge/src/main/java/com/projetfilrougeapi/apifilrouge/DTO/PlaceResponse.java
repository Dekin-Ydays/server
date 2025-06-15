package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlaceResponse {
    private Long id;
    private String name;
    private String address;
    private String type;
    private Location location;
    private int eventsCount;
    private long eventsPastCount;
    private String cityName;
    private String bannerUrl;
    private String imageUrl;
    private String text;

    public static PlaceResponse fromEntity(Place place) {
        long pastEvents = place.getEvents() != null ?
                place.getEvents().stream().filter(e -> e.getDate().isBefore(LocalDateTime.now())).count() : 0;

        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .type(place.getType())
                .location(new Location(place.getLatitude(), place.getLongitude()))
                .eventsCount(place.getEvents() != null ? place.getEvents().size() : 0)
                .eventsPastCount(pastEvents)
                .cityName(place.getCityName())
                .bannerUrl(place.getBannerUrl())
                .imageUrl(place.getImageUrl())
                .text(place.getContent())
                .build();
    }
}
