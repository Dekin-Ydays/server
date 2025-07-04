package com.projetfilrougeapi.apifilrouge.Specification;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventStatus;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventSpecification {

    /**
     * Returns a specification for events that are available :
     * - status is NOT_STARTED
     * - date is after now
     * - number of participants is less than maxCustomers
     */
    public static Specification<Event> isAvailable() {
        return (root, query, builder) -> builder.and(
                builder.equal(root.get("status"), EventStatus.NOT_STARTED),
                builder.greaterThan(root.get("date"), java.time.LocalDateTime.now()),
                builder.lt(builder.size(root.get("participants")), root.get("maxCustomers"))
        );
    }

    /**
     * Creates a specification to filter events that are marked as trending.
     * @return A JPA Specification for isTrending = true.
     */
    public static Specification<Event> isTrending() {
        return (root, query, builder) -> builder.isTrue(root.get("isTrending"));
    }

    /**
     * Creates a specification to filter events that occur between a start and end date.
     * @param start The start date of the range.
     * @param end The end date of the range.
     * @return A JPA Specification for the date range.
     */
    public static Specification<Event> hasDateBetween(LocalDate start, LocalDate end) {
        return (root, query, builder) -> {
            if (start != null && end != null) { // Si les deux filtres sont définis, alors on filtre entre les deux dates
                return builder.between(root.get("date"), start.atStartOfDay(), end.atTime(23, 59));
            } else if (start != null) { // si seulement la date début est fournie alors on filtre à partir de cette date
                return builder.greaterThanOrEqualTo(root.get("date"), start.atStartOfDay());
            } else if (end != null) { // Si seule la date de fin est fournie, on filtre jusqu'à cette date
                return builder.lessThanOrEqualTo(root.get("date"), end.atTime(23, 59));
            } else { // Aucun filtre à appliquer -> retourne une condition toujours vraie
                return builder.conjunction();
            }
        };
    }

    /**
     * Creates a specification to filter events within a given price range.
     * @param min The minimum price.
     * @param max The maximum price.
     * @return A JPA Specification for the price range.
     */
    public static Specification<Event> hasPriceBetween(Double min, Double max) {
        return (root, query, builder) -> {
            if (min != null && max != null) {
                return builder.between(root.get("price"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(root.get("price"), max);
            } else {
                return builder.conjunction();
            }
        };
    }

    /**
     * Creates a specification to filter events that belong to at least one of the specified categories.
     * This method is designed for a @ManyToMany relationship between Event and Category.
     * @param categoryKeys An array of category keys to filter by.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasCategories(String[] categoryKeys) {
        return (root, query, builder) -> {
            if (categoryKeys == null || categoryKeys.length == 0) {
                return builder.conjunction();
            }
            Join<Event, Category> categoryJoin = root.join("categories");
            query.distinct(true);
            return categoryJoin.get("key").in((Object[]) categoryKeys);
        };
    }


    /**
     * Creates a specification to filter events that occur in one or more specific cities by name.
     * @param cityNames An array of city names to search for.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasCityNames(String[] cityNames) {
        return (root, query, builder) -> {
            if (cityNames == null || cityNames.length == 0) {
                return builder.conjunction();
            }
            Join<Event, City> cityJoin = root.join("city");

            // On convertit les noms de villes de la requête en minuscules
            List<String> lowerCaseCityNames = Arrays.stream(cityNames)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // On compare avec le nom de la ville en base de données, également en minuscules
            return builder.lower(cityJoin.get("name")).in(lowerCaseCityNames);
        };
    }

    /**
     * Creates a specification to filter events that occur at one or more specific places by name.
     * @param placeNames An array of place names to search for.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasPlaceNames(String[] placeNames) {
        return (root, query, builder) -> {
            if (placeNames == null || placeNames.length == 0) {
                return builder.conjunction();
            }
            Join<Event, Place> placeJoin = root.join("place");

            List<String> lowerCasePlaceNames = Arrays.stream(placeNames)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            return builder.lower(placeJoin.get("name")).in(lowerCasePlaceNames);
        };
    }

    /**
     * Creates a specification to filter events by a specific place ID.
     * @param placeId The ID of the place.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasPlace(Long placeId) {
        return (root, query, builder) -> {
            if (placeId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("place").get("id"), placeId);
        };
    }

    /**
     * Creates a specification to filter events by a specific city ID.
     * @param cityId The ID of the city.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasCity(Long cityId) {
        return (root, query, builder) -> {
            if (cityId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("city").get("id"), cityId);
        };
    }

    /**
     * Filtre les événements qui sont marqués comme "première édition".
     * @return Une Specification pour JPA qui vérifie si le champ isFirstEdition est à true.
     */
    public static Specification<Event> isFirstEdition() {
        return (root, query, builder) -> builder.isTrue(root.get("isFirstEdition"));
    }

    /**
     * Creates a specification to search for text within the event's name.
     * The search is case-insensitive and matches partial text.
     * @param query The search term.
     * @return A JPA Specification.
     */
    public static Specification<Event> hasTextInName(String query) {
        return (root, cq, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction(); // Does nothing if the query is empty
            }
            return cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
        };
    }
}
