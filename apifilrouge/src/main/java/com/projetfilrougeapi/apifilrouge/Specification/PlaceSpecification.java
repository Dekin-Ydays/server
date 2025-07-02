package com.projetfilrougeapi.apifilrouge.Specification;

import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceSpecification {

    /**
     * Creates a specification to filter places by one or more types.
     * The search is case-insensitive.
     * @param types An array of place types to search for.
     * @return A JPA Specification.
     */
    public static Specification<Place> hasTypes(String[] types) {
        return (root, query, builder) -> {
            if (types == null || types.length == 0) {
                return builder.conjunction();
            }
            List<String> lowerCaseTypes = Arrays.stream(types)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            return builder.lower(root.get("type")).in(lowerCaseTypes);
        };
    }

    /**
     * Creates a specification to filter places by one or more city names.
     * The search is case-insensitive.
     * @param cityNames An array of city names to search for.
     * @return A JPA Specification.
     */
    public static Specification<Place> hasCityNames(String[] cityNames) {
        return (root, query, builder) -> {
            if (cityNames == null || cityNames.length == 0) {
                return builder.conjunction();
            }
            Join<Place, City> cityJoin = root.join("city");
            List<String> lowerCaseCityNames = Arrays.stream(cityNames)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            return builder.lower(cityJoin.get("name")).in(lowerCaseCityNames);
        };
    }

}
