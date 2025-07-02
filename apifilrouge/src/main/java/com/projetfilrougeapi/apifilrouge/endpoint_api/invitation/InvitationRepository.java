package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@RepositoryRestResource(collectionResourceRel = "invitations", path = "invitations",exported = false)
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findById(Long id);

    List<Invitation> findAllByOrganizerId(Long organizerId);

    /**
     * Finds a specific invitation based on the unique combination
     * of event and user.
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return An Optional containing the invitation if it exists.
     */
    Optional<Invitation> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Checks if an invitation for a user
     * to a specific event already exists.
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return true if the invitation exists, false otherwise.
     */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

}
