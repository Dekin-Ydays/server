package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cities", path = "cities", exported = false)
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findById(Long id);

    /**
     * Get the city by the slug, optional because the city could not exist
     *
     * @param slug
     * @return
     */
    Optional<City> findBySlug(String slug);

    List<City> findByRegionIgnoreCase(String region);

    /**
     * Searches for cities whose name contains the provided string,
     * ignoring case.
     *
     * @param query The string to search for.
     * @return A list of matching cities.
     */
    Page<City> findByNameContainingIgnoreCase(String query, Pageable pageable);

}
