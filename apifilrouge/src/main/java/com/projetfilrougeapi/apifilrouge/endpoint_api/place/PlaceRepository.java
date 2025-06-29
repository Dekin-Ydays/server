package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "places", path = "places",exported = false)
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);

    Optional<Place> findBySlug(String slug);

    Page<Place> findAll(Pageable pageable);

    boolean existsByNameAndAddressAndLatitudeAndLongitude(String name, String address, Double latitude, Double longitude);

    /**
     * Searches for places whose name contains the provided string,
     * ignoring case.
     * @param query The string to search for.
     * @return A list of matching places.
     */
    Page<Place> findByNameContainingIgnoreCase(String query, Pageable pageable);

}