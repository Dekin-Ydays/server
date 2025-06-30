package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {

    @NotNull(message = "The event date is required.")
    @Future(message = "The event date must be in the future.")
    private LocalDateTime date;

    @Size(max = 1000, message = "The description cannot exceed 1000 characters.")
    private String description;

    @NotBlank(message = "The event name cannot be empty.")
    @Size(min = 3, max = 150, message = "The name must be between 3 and 150 characters.")
    private String name;

    private String address;

    @NotNull(message = "Maximum number of customers is required.")
    @Positive(message = "Maximum customers must be a positive number.")
    private Integer maxCustomers;

    private Boolean isTrending;

    private Boolean isFirstEdition;

    private Boolean isInvitationOnly;

    private EventStatus status;

    @PositiveOrZero(message = "The price cannot be negative.")
    private Double price;

    @NotNull(message = "A place ID is required.")
    @Positive(message = "City ID must be a positive number.")
    private Long placeId;

    @NotNull(message = "A city ID is required.")
    @Positive(message = "City ID must be a positive number.")
    private Long cityId;

    private List<String> categoryKeys;

    private List<Long> participantIds;

    private int currentParticipants;

    private String contentHtml;

    private String imageUrl;
}