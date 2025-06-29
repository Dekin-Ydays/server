package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
     * Performs a non-paginated search by delegating to the paginated method.
     */
    @Override
    public List<SearchResultResponse> search(String query) {
        return search(query, Pageable.unpaged()).getContent();
    }

    /**
     * Performs a paginated search for events based on the query.
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByNameContainingIgnoreCase(query, pageable);

        return eventPage.map(event -> SearchResultResponse.builder()
                .type("event")
                .event(EventSummaryResponse.fromEntity(event))
                .build());
    }
}
