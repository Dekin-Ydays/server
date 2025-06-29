package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "users", path = "users",exported = false)
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    //User findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    /**
     * Retrieves a list of all unique organizers who have at least
     * one event in the specified city.
     *
     * @param cityId The ID of the city.
     * @return A list of unique User entities.
     */
    @Query("SELECT DISTINCT e.organizer FROM Event e WHERE e.city.id = :cityId")
    List<User> findOrganizersByCity(@Param("cityId") Long cityId);

    /**
     * Retrieves a list of all unique organizers who have at least
     * one event in the specified place.
     *
     * @param placeId The ID of the place.
     * @return A list of unique User entities.
     */
    @Query("SELECT DISTINCT e.organizer FROM Event e WHERE e.place.id = :placeId")
    List<User> findOrganizersByPlace(@Param("placeId") Long placeId);

    /**
     * Retrieves a list of users by their role.
     *
     * @param role The role to filter users by.
     * @return A list of users with the specified role.
     */
    List<User> findByRole(Role role);
}