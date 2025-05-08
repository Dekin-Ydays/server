package com.projetfilrougeapi.apifilrouge.endpoint_api.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
@Repository
public interface CityRepository extends JpaRepository<City, Long>{
    Optional<City> findById(Long id);
}
