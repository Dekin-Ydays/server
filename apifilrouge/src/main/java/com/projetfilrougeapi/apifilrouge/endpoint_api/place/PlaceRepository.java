package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "places", path = "places",exported = false)
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);
    Optional<Place> findBySlug(String slug);
    boolean existsByNameAndAddressAndLatitudeAndLongitude(String name, String address, Double latitude, Double longitude);
    List<Place> findByNameContainingIgnoreCase(String query);

}