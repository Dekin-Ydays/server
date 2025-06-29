package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
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
public class EventSearchProvider implements SearchProvider {

    private final EventRepository eventRepository;

    /**
     * Checks if this provider supports the "event" search type.
     */
    @Override
    public boolean supports(String type) {
        return "event".equalsIgnoreCase(type);
    }

    /**
     * Implements the limited search.
     */
    @Override
    public List<SearchResultResponse> search(String query, int limit, Role... roles) {
        // Create a Pageable to request only the first 'limit' results.
        Pageable pageRequest = PageRequest.of(0, limit);
        // Call the paginated repository method and return only the content.
        return eventRepository.findByNameContainingIgnoreCase(query, pageRequest)
                .map(event -> SearchResultResponse.builder()
                        .type("event")
                        .event(EventSummaryResponse.fromEntity(event))
                        .build())
                .getContent();
    }

    /**
     * Performs a paginated search for events based on the query.
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable, Role... roles) {
        Page<Event> eventPage = eventRepository.findByNameContainingIgnoreCase(query, pageable);

        return eventPage.map(event -> SearchResultResponse.builder()
                .type("event")
                .event(EventSummaryResponse.fromEntity(event))
                .build());
    }
}
