package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlaceSearchProvider implements SearchProvider {

    private final PlaceRepository placeRepository;

    /**
     * Checks if this provider supports the "place" search type.
     */
    @Override
    public boolean supports(String type) {
        return "place".equalsIgnoreCase(type);
    }

    /**
     * Performs a non-paginated search by delegating to the paginated method.
     * This is used for the global search feature.
     */
    @Override
    public List<SearchResultResponse> search(String query, int limit) {
        Pageable pageRequest = PageRequest.of(0, limit);
        return placeRepository.findByNameContainingIgnoreCase(query, pageRequest)
                .map(place -> SearchResultResponse.builder()
                        .type("place")
                        .place(PlaceResponse.fromEntity(place))
                        .build())
                .getContent();
    }

    /**
     * Performs a paginated search for places based on the query.
     * This is used for typed searches (e.g., ?types=place).
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable) {
        // We call the paginated search method on the repository.
        Page<Place> placePage = placeRepository.findByNameContainingIgnoreCase(query, pageable);

        // We convert the page of entities into a page of response DTOs.
        return placePage.map(place -> SearchResultResponse.builder()
                .type("place")
                .place(PlaceResponse.fromEntity(place))
                .build());
    }
}
