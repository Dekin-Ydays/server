package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);
    Optional<Place> findByNameIgnoreCase(String name);
    boolean existsByNameAndAddressAndLatitudeAndLongitude(String name, String address, Double latitude, Double longitude);
}