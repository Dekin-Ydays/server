package com.projetfilrougeapi.apifilrouge.endpoint_api.search.providers;

import com.projetfilrougeapi.apifilrouge.DTO.SearchResultResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.Specification.UserSpecification;
import com.projetfilrougeapi.apifilrouge.endpoint_api.search.contracts.SearchProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserSearchProvider implements SearchProvider {

    private final UserRepository userRepository;

    @Override
    public boolean supports(String type) {
        return "user".equalsIgnoreCase(type);
    }

    @Override
    public List<SearchResultResponse> search(String query) {
        // C'est ici que toute la logique de recherche complexe est appelée.
        Specification<User> spec = UserSpecification.hasTextInNameOrPseudo(query);

        return userRepository.findAll(spec).stream()
                .map(user -> SearchResultResponse.builder()
                        .type("user")
                        .user(UserResponse.fromEntity(user))
                        .build())
                .collect(Collectors.toList());
    }
}