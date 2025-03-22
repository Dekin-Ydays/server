package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import java.util.List;
@RepositoryRestResource(collectionResourceRel = "events", path = "events")
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByActiveTrue();
}
