package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.assembler.SearchResultAssembler;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final List<SearchProvider> searchProviders;
    private final PagedResourcesAssembler<SearchResultResponse> pagedResourcesAssembler;
    private final SearchResultAssembler searchResultAssembler;

    /**
     * Performs a global search across multiple types, returning a simple, limited list.
     * @param query The search term.
     * @param types The types to search within. If empty, searches all types.
     * @param limit The max number of results to return.
     * @return A list of shuffled search results.
     */
    public List<SearchResultResponse> globalSearch(String query, String[] types, int limit) {
        List<SearchResultResponse> allResults = new ArrayList<>();
        Set<String> searchTypes = (types == null || types.length == 0)
                ? Set.of("city", "place", "event", "user")
                : new HashSet<>(Arrays.asList(types));

        for (SearchProvider provider : searchProviders) {
            if (searchTypes.stream().anyMatch(provider::supports)) {
                allResults.addAll(provider.search(query));
            }
        }

        // Shuffle the combined results for discovery and apply the limit.
        Collections.shuffle(allResults);
        return allResults.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Performs a paginated search for a single, specific type.
     * @param query The search term.
     * @param type The specific type to search for.
     * @param pageable Pagination information.
     * @return A PagedModel of the search results.
     */
    public PagedModel<EntityModel<SearchResultResponse>> typedSearch(String query, String type, Pageable pageable) {
        // Find the one provider that supports this type.
        SearchProvider provider = searchProviders.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown search type: " + type));

        // Execute the paginated search.
        Page<SearchResultResponse> resultPage = provider.search(query, pageable);

        // Return the paginated HATEOAS model.
        return pagedResourcesAssembler.toModel(resultPage, searchResultAssembler);
    }
}
