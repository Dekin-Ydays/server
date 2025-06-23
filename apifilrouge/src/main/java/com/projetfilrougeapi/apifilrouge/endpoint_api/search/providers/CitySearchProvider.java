package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CitySearchProvider implements SearchProvider {

    private final CityRepository cityRepository;

    @Override
    public boolean supports(String type) {
        return "city".equalsIgnoreCase(type);
    }

    @Override
    public List<SearchResultResponse> search(String query) {
        return cityRepository.findByNameContainingIgnoreCase(query).stream()
                .map(city -> SearchResultResponse.builder()
                        .type("city")
                        .city(CityResponse.fromEntity(city))
                        .build())
                .collect(Collectors.toList());
    }
}
