package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
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
public class EventSummaryResponse {
    private Long id;
    private LocalDateTime date;
    private String description;
    private String name;
    private String address;
    private Integer maxCustomers;
    private Boolean isTrending;
    private Boolean isFirstEdition;
    private Double price;
    private EventStatus status;
    private String imageUrl;
    private int currentParticipants;
    private String cityName;
    private String placeName;
    private List<CategorySummary> categories;
    private OrganizerSummary organizer;

    public static EventSummaryResponse fromEntity(Event event) { // on ne veut pas pouvoir instancier un objet vide de EventResponse
        String placeName = (event.getPlace() != null) ? event.getPlace().getName() : null;
        String cityName = (event.getPlace() != null) ? event.getPlace().getCityName() : null;
        List<CategorySummary> categories = event.getCategories().stream().map(category -> new CategorySummary(category.getName(), category.getKey())).collect(Collectors.toList());
        OrganizerSummary organizer = (event.getOrganizer() != null) ? OrganizerSummary.builder().pseudo(event.getOrganizer().getPseudo()).imageUrl(event.getOrganizer().getImageUrl()).note(event.getOrganizer().getNote()).firstName(event.getOrganizer().getFirstName()).lastName(event.getOrganizer().getLastName()).build() : null;

        return EventSummaryResponse.builder()
                .id(event.getId())
                .date(event.getDate())
                .description(event.getDescription())
                .name(event.getName())
                .address(event.getAddress())
                .maxCustomers(event.getMaxCustomers())
                .isTrending(event.getIsTrending())
                .isFirstEdition(event.getIsFirstEdition())
                .price(event.getPrice())
                .status(event.getStatus())
                .imageUrl(event.getImageUrl())
                .currentParticipants(event.getParticipants().size())
                .placeName(placeName)
                .cityName(cityName)
                .categories(categories)
                .organizer(organizer)
                .build();
    }
}