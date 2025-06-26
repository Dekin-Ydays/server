package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public List<SearchResultResponse> search(
            @RequestParam(value = "query") String query,
            @RequestParam(required = false, value = "types") String[] types
    ) {
        return searchService.search(query, types);
    }
}
