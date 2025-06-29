package com.projetfilrougeapi.apifilrouge.Specification;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    /**
     * Creates a specification that searches for a string in the user's username,
     * first name, or last name, ignoring case.
     *
     * @param query The user's search term.
     * @return A JPA Specification.
     */
    public static Specification<User> hasTextInNameOrPseudo(String query) {
        return (root, cq, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }

            String[] words = query.toLowerCase().split("\\s+");

            List<Predicate> predicates = new ArrayList<>();
            for (String word : words) {
                Predicate pPseudo = cb.like(cb.lower(root.get("pseudo")), "%" + word + "%");
                Predicate pFirstName = cb.like(cb.lower(root.get("firstName")), "%" + word + "%");
                Predicate pLastName = cb.like(cb.lower(root.get("lastName")), "%" + word + "%");
                predicates.add(cb.or(pPseudo, pFirstName, pLastName));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to filter users by one or more roles.
     * This method will be used by the SearchService to apply permission logic.
     *
     * @param roles The roles to filter by.
     * @return A JPA Specification.
     */
    public static Specification<User> hasRole(Role... roles) {
        return (root, query, builder) -> {
            if (roles == null || roles.length == 0) {
                return builder.conjunction();
            }
            // Uses the 'in' clause to match any of the provided roles
            return root.get("role").in((Object[]) roles);
        };
    }
}
