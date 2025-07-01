package com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface defining a contract for a search provider.
 */
public interface SearchProvider {

    /**
     * Indicates whether this provider can handle the requested search type.
     *
     * @param type The type to check.
     */
    boolean supports(String type);

    /**
     * Performs a non-paginated search, returning all results.
     * This is used for the global search feature, where results from all providers
     * are combined, shuffled, and then limited.
     * @param query The search term.
     * @param limit the limitation of results by type.
     * @return A list of all matching search results.
     */
    List<SearchResultResponse> search(String query, int limit,  Role... roles);

    /**
     * Performs a paginated search.
     * This is used for typed searches (e.g., ?types=user), providing full
     * pagination capabilities for a specific entity type.
     * @param query The search term.
     * @param pageable The pagination information (page, size, sort).
     * @return A Page of search results.
     */
    Page<SearchResultResponse> search(String query, Pageable pageable, Role... roles);
}
