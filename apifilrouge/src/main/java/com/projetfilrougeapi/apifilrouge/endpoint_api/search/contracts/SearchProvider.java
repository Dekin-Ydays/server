package com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;

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
     * Performs the search for the given term.
     *
     * @param query The search term.
     */
    List<SearchResultResponse> search(String query);
}
