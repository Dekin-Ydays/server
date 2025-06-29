package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.Specification.UserSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserSearchProvider implements SearchProvider {

    private final UserRepository userRepository;

    /**
     * Checks if this provider supports the given search type.
     * @param type The search type.
     * @return true if the type is supported, false otherwise.
     */
    @Override
    public boolean supports(String type) {
        return "user".equalsIgnoreCase(type);
    }

    /**
     * Performs a non-paginated search, used for the global search feature.
     * It delegates to the paginated search method, requesting all results.
     * @param query The search term.
     * @return A list of all matching search results.
     */
    @Override
    public List<SearchResultResponse> search(String query) {
        return search(query, Pageable.unpaged()).getContent();
    }
    /**
     * Performs a paginated search for users based on the query.
     * This is used for typed searches.
     * @param query The search term.
     * @param pageable The pagination information.
     * @return A Page of search results.
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable) {
        // We use the UserSpecification to build the advanced search query.
        Specification<User> spec = UserSpecification.hasTextInNameOrPseudo(query);

        // We call the repository's findAll method, which handles the Specification and Pageable.
        Page<User> userPage = userRepository.findAll(spec, pageable);

        // We convert the page of entities into a page of response DTOs.
        return userPage.map(user -> SearchResultResponse.builder().type("user").user(UserResponse.fromEntity(user)).build());
    }

}