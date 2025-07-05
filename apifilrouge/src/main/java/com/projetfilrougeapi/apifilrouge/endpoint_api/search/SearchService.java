package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.assembler.SearchResultAssembler;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final List<SearchProvider> searchProviders;
    private final PagedResourcesAssembler<SearchResultResponse> pagedResourcesAssembler;
    private final SearchResultAssembler searchResultAssembler;
    private final EventRepository eventRepository;

    /**
     * Performs a global search across multiple types, returning a limited list.
     *
     * @param query        The search term.
     * @param types        The types to search within. If empty, searches all types.
     * @param limitPerType The max number of results to return per type.
     * @return A list of shuffled search results.
     */
    public List<SearchResultResponse> globalSearch(String query, String[] types, int limitPerType) {
        List<SearchResultResponse> allResults = new ArrayList<>();
        Set<String> searchTypes = (types == null || types.length == 0)
                ? Set.of("city", "place", "event", "organizer")
                : new HashSet<>(Arrays.asList(types));

        // Handle searches for non-user types.
        for (SearchProvider provider : searchProviders) {
            if (searchTypes.stream().anyMatch(provider::supports)) {
                allResults.addAll(provider.search(query, limitPerType));
            }
        }
        Collections.shuffle(allResults);
        return allResults;
    }

    /**
     * Performs a paginated search for a single, specific type.
     * This method now handles special cases for "event" and "user" searches
     * to apply additional filters and permission logic.
     *
     * @param query    The search term.
     * @param type     The specific type to search for.
     * @param pageable Pagination information.
     * @param cities   Optional filter for event search.
     * @param places   Optional filter for event search.
     * @return A PagedModel of the search results.
     */
    public PagedModel<EntityModel<SearchResultResponse>> typedSearch(String query, String type, Pageable pageable, String[] cities, String[] places) {

        if ("event".equalsIgnoreCase(type)) {
            Specification<Event> spec = EventSpecification.hasTextInName(query);
            if (cities != null && cities.length > 0) {
                spec = spec.and(EventSpecification.hasCityNames(cities));
            }
            if (places != null && places.length > 0) {
                spec = spec.and(EventSpecification.hasPlaceNames(places));
            }
            Page<Event> eventPage = eventRepository.findAll(spec, pageable);
            Page<SearchResultResponse> resultPage = eventPage.map(event -> SearchResultResponse.builder()
                    .type("event").event(EventSummaryResponse.fromEntity(event)).build());
            return pagedResourcesAssembler.toModel(resultPage, searchResultAssembler);
        }

        // Standard logic for other types (city, place).
        else {
            SearchProvider provider = searchProviders.stream()
                    .filter(p -> p.supports(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown or unsupported search type: " + type));
            Page<SearchResultResponse> resultPage = provider.search(query, pageable);
            return pagedResourcesAssembler.toModel(resultPage, searchResultAssembler);
        }
    }

}
