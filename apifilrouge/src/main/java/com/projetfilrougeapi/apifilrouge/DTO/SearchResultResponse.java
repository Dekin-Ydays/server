package com.projetfilrougeapi.apifilrouge.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO acting as a container for a search result.
 * It includes the result type and the corresponding detailed DTO object,
 * allowing reuse of the rich DTOs already created.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResultResponse {

    private String type;

    private CityResponse city;
    private PlaceResponse place;
    private EventSummaryResponse event;
    private UserResponse user;
}
