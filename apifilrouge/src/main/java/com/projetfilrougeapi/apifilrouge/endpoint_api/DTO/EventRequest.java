package com.projetfilrougeapi.apifilrouge.endpoint_api.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {
    private LocalDateTime eventDate;
    private String description;
    private String eventName;
    private String address;
    private Integer maxCustomers;
    private Boolean isTrending;
    private EventStatus eventStatus;
    private Long placeId;
    private List<Long> categoryIds;
}

