package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);
    Optional<Place> findBySlug(String slug);
    boolean existsByNameAndAddressAndLatitudeAndLongitude(String name, String address, Double latitude, Double longitude);
    List<Place> findByNameContainingIgnoreCase(String query);

}