package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Global search endpoint that adapts based on the provided parameters.
     * - If a single 'type' is specified, it performs a paginated search for that type.
     * - Otherwise, it performs a non-paginated global search with a limit per type.
     *
     * @param query The user's search term (required).
     * @param types An optional array of types to search for (e.g., "event", "city"). If not provided, searches all types.
     * @param limitPerType The maximum number of results to return per type for a global search (default: 3).
     * @param cities An optional array of city names to filter events.
     * @param places An optional array of place names to filter events.
     * @param pageable The pagination object (page, size, sort) for typed searches.
     * @return A response containing either a limited list (global search) or a paginated model (typed search).
     */
    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(required = false) String[] types,
            @RequestParam(defaultValue = "3", name = "limitPerType") int limitPerType,
            @RequestParam(required = false) String[] cities,
            @RequestParam(required = false) String[] places,
            Pageable pageable
    ) {
        boolean isSingleTypeSearch = types != null && types.length == 1;

        if (isSingleTypeSearch) {
            // Pass the filters to the typed search.
            return ResponseEntity.ok(searchService.typedSearch(query, types[0], pageable, cities, places));
        } else {
            return ResponseEntity.ok(searchService.globalSearch(query, types, limitPerType));
        }
    }
}
