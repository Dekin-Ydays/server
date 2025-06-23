package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.PlaceResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlaceSearchProvider implements SearchProvider {

    private final PlaceRepository placeRepository;

    @Override
    public boolean supports(String type) {
        return "place".equalsIgnoreCase(type);
    }

    @Override
    public List<SearchResultResponse> search(String query) {
        return placeRepository.findByNameContainingIgnoreCase(query).stream()
                .map(place -> SearchResultResponse.builder()
                        .type("place")
                        .place(PlaceResponse.fromEntity(place))
                        .build())
                .collect(Collectors.toList());
    }
}