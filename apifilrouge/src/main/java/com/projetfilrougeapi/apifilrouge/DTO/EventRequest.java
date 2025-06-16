package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

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
    private Double price;
    private Long placeId;
    private Long cityId;
    private List<String> categoryKeys;
    private List<Long> participantIds;
    private int currentParticipants;
    private String contentHtml;
    private String imageUrl;
}