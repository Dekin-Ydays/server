package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "places", path = "places")
@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    Optional<Place> findById(Integer id);
}
