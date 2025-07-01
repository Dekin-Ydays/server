package com.projetfilrougeapi.apifilrouge.endpoint_api.search;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.Specification.UserSpecification;
import com.projetfilrougeapi.apifilrouge.assembler.SearchResultAssembler;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final List<SearchProvider> searchProviders;
    private final PagedResourcesAssembler<SearchResultResponse> pagedResourcesAssembler;
    private final SearchResultAssembler searchResultAssembler;
    private final UserRepository userRepository;

    /**
     * Performs a global search across multiple types, returning a limited list.
     *
     * @param query        The search term.
     * @param types        The types to search within. If empty, searches all types.
     * @param limitPerType The max number of results to return per type.
     * @return A list of shuffled search results.
     */
    public List<SearchResultResponse> globalSearch(String query, String[] types, int limitPerType) {
        List<SearchResultResponse> allResults = new ArrayList<>();
        Set<String> searchTypes = (types == null || types.length == 0)
                ? Set.of("city", "place", "event", "user", "organizer")
                : new HashSet<>(Arrays.asList(types));

        // Handle searches for non-user types.
        for (SearchProvider provider : searchProviders) {
            if (!provider.supports("user") && !provider.supports("organizer") && searchTypes.stream().anyMatch(provider::supports)) {
                allResults.addAll(provider.search(query, limitPerType));
            }
        }

        // Specific logic for user/organizer search
        if (searchTypes.contains("user") || searchTypes.contains("organizer")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Specification<User> userSpec = UserSpecification.hasTextInNameOrPseudo(query);

            // If the user is NOT authenticated OR is not an Admin/AuthService, they can only see Organizers.
            if (!(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
                User currentUser = (User) authentication.getPrincipal();
                if (currentUser.getRole() != Role.Admin && currentUser.getRole() != Role.AuthService) {
                    userSpec = userSpec.and(UserSpecification.hasRole(Role.Organizer));
                }
            } else {
                // Anonymous user can only search for organizers
                userSpec = userSpec.and(UserSpecification.hasRole(Role.Organizer));
            }

            List<User> foundUsers = userRepository.findAll(userSpec, PageRequest.of(0, limitPerType)).getContent();
            foundUsers.forEach(user -> allResults.add(
                    SearchResultResponse.builder()
                            .type("user")
                            .user(UserResponse.fromEntity(user))
                            .build()
            ));
        }

        Collections.shuffle(allResults);
        return allResults;
    }

    /**
     * Performs a paginated search for a single type, applying permission-based logic for users.
     *
     * @param query    The search term.
     * @param type     The specific type to search for.
     * @param pageable Pagination information.
     * @return A PagedModel of the search results.
     */
    public PagedModel<EntityModel<SearchResultResponse>> typedSearch(String query, String type, Pageable pageable) {

        if ("user".equalsIgnoreCase(type) || "organizer".equalsIgnoreCase(type)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Specification<User> spec = UserSpecification.hasTextInNameOrPseudo(query);

            if (!(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
                User currentUser = (User) authentication.getPrincipal();
                if (currentUser.getRole() != Role.Admin && currentUser.getRole() != Role.AuthService) {
                    spec = spec.and(UserSpecification.hasRole(Role.Organizer));
                } else if ("organizer".equalsIgnoreCase(type)) {
                    spec = spec.and(UserSpecification.hasRole(Role.Organizer));
                }
            } else {
                // Anonymous user can only search for organizers
                spec = spec.and(UserSpecification.hasRole(Role.Organizer));
            }

            Page<User> userPage = userRepository.findAll(spec, pageable);
            Page<SearchResultResponse> resultPage = userPage.map(user -> SearchResultResponse.builder()
                    .type("user").user(UserResponse.fromEntity(user)).build());

            return pagedResourcesAssembler.toModel(resultPage, searchResultAssembler);
        }

        // Standard logic for other types (city, place, event).
        SearchProvider provider = searchProviders.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown search type: " + type));

        Page<SearchResultResponse> resultPage = provider.search(query, pageable);
        return pagedResourcesAssembler.toModel(resultPage, searchResultAssembler);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     * This method encapsulates the logic you provided.
     *
     * @return The authenticated User entity.
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database for email: " + userEmail));
    }
}
