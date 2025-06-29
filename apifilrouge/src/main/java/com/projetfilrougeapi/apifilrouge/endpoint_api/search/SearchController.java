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
     * Global search endpoint that dispatches to the correct service logic based on parameters.
     * - If exactly one 'type' is provided, it performs a paginated search.
     * - Otherwise, it performs a global search with a simple limit.
     */
    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(required = false) String[] types,
            @RequestParam(defaultValue = "10") int limit,
            Pageable pageable
    ) {
        boolean isSingleTypeSearch = types != null && types.length == 1;

        if (isSingleTypeSearch) {
            // Typed search -> return a paginated response
            return ResponseEntity.ok(searchService.typedSearch(query, types[0], pageable));
        } else {
            // Global search -> return a limited list
            return ResponseEntity.ok(searchService.globalSearch(query, types, limit));
        }
    }
}
