package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.Specification.UserSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     *
     * @param type The search type.
     * @return true if the type is supported, false otherwise.
     */
    @Override
    public boolean supports(String type) {
        return "user".equalsIgnoreCase(type) || "organizer".equalsIgnoreCase(type);
    }

    /**
     * Performs a limited search for the global search feature.
     * It uses the UserSpecification and a PageRequest to fetch only the top 'limit' results.
     *
     * @param query The search term.
     * @param limit The max number of results to return per type.
     * @return A list of all matching search results.
     */
    @Override
    public List<SearchResultResponse> search(String query, int limit, Role... roles) {
        Pageable pageRequest = PageRequest.of(0, limit);
        Specification<User> spec = UserSpecification.hasTextInNameOrPseudo(query);

        if (roles != null && roles.length > 0) {
            spec = spec.and(UserSpecification.hasRole(roles));
        }

        return userRepository.findAll(spec, pageRequest)
                .map(user -> SearchResultResponse.builder()
                        .type("user")
                        .user(UserResponse.fromEntity(user))
                        .build())
                .getContent();
    }

    /**
     * Performs a paginated search for users based on the query.
     * This is used for typed searches.
     *
     * @param query    The search term.
     * @param pageable The pagination information.
     * @return A Page of search results.
     */
    @Override
    public Page<SearchResultResponse> search(String query, Pageable pageable, Role... roles) {
        Specification<User> spec = UserSpecification.hasTextInNameOrPseudo(query);

        if (roles != null && roles.length > 0) {
            spec = spec.and(UserSpecification.hasRole(roles));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(user -> SearchResultResponse.builder()
                .type("user")
                .user(UserResponse.fromEntity(user))
                .build());
    }

}