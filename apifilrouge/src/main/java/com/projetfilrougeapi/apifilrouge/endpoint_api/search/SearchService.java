package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    // Spring injecte automatiquement TOUS les beans (toutes les classes) qui implémentent SearchProvider
    private final List<SearchProvider> searchProviders;

    public List<SearchResultResponse> search(String query, String[] types) {
        List<SearchResultResponse> results = new ArrayList<>();

        Set<String> searchTypes = (types == null || types.length == 0) ? Set.of("city", "place", "event", "user") : new HashSet<>(Arrays.asList(types));

        for (SearchProvider provider : searchProviders) {
            if (searchTypes.stream().anyMatch(provider::supports)) {
                results.addAll(provider.search(query));
            }
        }

        return results;
    }
}
