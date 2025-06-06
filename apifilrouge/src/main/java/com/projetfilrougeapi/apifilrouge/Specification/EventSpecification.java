package com.projetfilrougeapi.apifilrouge.Specification;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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
}
