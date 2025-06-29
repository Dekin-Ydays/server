package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.CityResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CitySearchProvider implements SearchProvider {

    private final CityRepository cityRepository;

    /**
     * Checks if this provider supports the "city" search type.
     */
    @Override
    public boolean supports(String type) {
        return "city".equalsIgnoreCase(type);
    }

    /**
     * Performs a non-paginated search by delegating to the paginated method.
     */
    @Override
    public List<SearchResultResponse> search(String query) {
        return search(query, Pageable.unpaged()).getContent();
    }

    /**
     * Performs a paginated search for cities based on the query.
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable) {
        Page<City> cityPage = cityRepository.findByNameContainingIgnoreCase(query, pageable);

        return cityPage.map(city -> SearchResultResponse.builder()
                .type("city")
                .city(CityResponse.fromEntity(city))
                .build());
    }
}
