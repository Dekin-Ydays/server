package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private LocalDateTime date;
    private String description;
    private String name;
    private String address;
    private Integer maxCustomers;
    private Boolean isTrending;
    private Double price;
    private EventStatus status;
    private int currentParticipants;

    public static EventResponse fromEntity(Event event) { // on ne veux pas pouvoir instancier un objet vide de EventResponse

        return EventResponse.builder()
                .id(event.getId())
                .date(event.getDate())
                .description(event.getDescription())
                .name(event.getName())
                .address(event.getAddress())
                .maxCustomers(event.getMaxCustomers())
                .isTrending(event.getIsTrending())
                .price(event.getPrice())
                .status(event.getStatus())
                .currentParticipants(event.getParticipants().size())
                .build();
    }
}