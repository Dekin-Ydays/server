package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventSearchProvider implements SearchProvider {

    private final EventRepository eventRepository;

    @Override
    public boolean supports(String type) {
        return "event".equalsIgnoreCase(type);
    }

    @Override
    public List<SearchResultResponse> search(String query) {
        return eventRepository.findByNameContainingIgnoreCase(query).stream()
                .map(event -> SearchResultResponse.builder()
                        .type("event")
                        .event(EventSummaryResponse.fromEntity(event))
                        .build())
                .collect(Collectors.toList());
    }
}