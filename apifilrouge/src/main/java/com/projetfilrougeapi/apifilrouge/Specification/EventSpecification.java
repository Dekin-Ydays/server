package com.projetfilrougeapi.apifilrouge.Specification;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventSpecification {

    // filtre les événements selon une plage de dates
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

    // filtre les événements selon une fourchette de prix
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
     * Filtre les événements qui appartiennent à au moins une des catégories spécifiées.
     * Cette méthode est conçue pour une relation @ManyToMany entre Event et Category.
     *
     * @param categoryKeys Un tableau de noms de catégories sur lesquels filtrer.
     * @return Une Specification pour JPA.
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
     * Filtre les événements qui ont lieu dans une ou plusieurs villes spécifiques, par leur nom.
     * @param cityNames Un tableau de noms de villes à rechercher.
     * @return Une Specification pour JPA.
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
     * Filtre les événements qui ont lieu dans un ou plusieurs lieux spécifiques, par leur nom.
     * @param placeNames Un tableau de noms de lieux à rechercher.
     * @return Une Specification pour JPA.
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
     * getEventsForPlace
     * @param placeId
     * @return
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
     * getEventsForCity
     * @param cityId
     * @return
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
}
