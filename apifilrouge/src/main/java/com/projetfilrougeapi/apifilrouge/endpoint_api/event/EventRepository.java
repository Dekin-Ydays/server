package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findById(Long id);
    boolean existsByOrganizerIdAndCityId(Long organizerId, Long cityId);

     // En surchargeant la méthode findAll, on dit à Spring d'appliquer
     // notre Entity Graph nommé "Event.withDetails" chaque fois que cette méthode est appelée.
     // Le type FETCH assure que le JOIN FETCH sera utilisé.
     // Spring se charge de combiner la spécification (pour le WHERE) et le graphe (pour les JOIN FETCH).

    @Override
    @EntityGraph(value = "Event.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    /**
     * Get all the events for a city
     * in one query
     */
    @EntityGraph(value = "Event.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Event> findByCityId(Long cityId);

    /**
     * Searches for events whose name contains the provided string,
     * ignoring case.
     * @param query The string to search for.
     * @return A list of matching events.
     */
    List<Event> findByNameContainingIgnoreCase(String query);

    /**
     * Get all the events for a place
     * in one query
     */
    @EntityGraph(value = "Event.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Event> findByPlaceId(Long placeId);
}

