package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {
    private LocalDateTime date;
    private String description;
    private String name;
    private String address;
    private Integer maxCustomers;
    private Boolean isTrending;
    private EventStatus status;
    private Long placeId;
    private List<Long> categoryIds;
}

