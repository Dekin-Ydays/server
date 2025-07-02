package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.DTO.EventResponse;
import com.projetfilrougeapi.apifilrouge.DTO.InvitationRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Invitation>> getAllInvitations() {
        return invitationService.getAllInvitations();
    }
    /**
     * Endpoint to find a specific invitation by event and user IDs.
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return The matching invitation.
     */
    @GetMapping("/invitations/search")
    public EntityModel<Invitation> findInvitationByEventAndUser(
            @RequestParam("event_id") Long eventId,
            @RequestParam("user_id") Long userId
    ) {
        return invitationService.getInvitationByEventAndUser(eventId, userId);
    }

    @GetMapping("/{id}")
    public EntityModel<Invitation> getInvitationById(@PathVariable("id") Long id) {
        return invitationService.getInvitationById(id);
    }

    /**
     * Returns the event (as DTO) associated with a specific invitation.
     *
     * @param id The ID of the invitation.
     * @return An EntityModel wrapping the EventResponse DTO.
     */
    @GetMapping("/{id}/event")
    public EntityModel<EventResponse> getEventForInvitation(@PathVariable Long id) {
        return invitationService.getEventForInvitation(id);
    }

    /**
     * Retrieves the user associated with a given invitation.
     *
     * @param invitationId the ID of the invitation
     * @return an EntityModel wrapping the User linked to the invitation
     */
    @GetMapping("/{invitationId}/user")
    public EntityModel<UserResponse> getUserForInvitation(@PathVariable Long invitationId) {
        return invitationService.getUserForInvitation(invitationId);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Invitation> updateInvitation(@PathVariable Long id, @RequestBody InvitationRequest invitation) {
        return invitationService.updateInvitation(id, invitation);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Invitation> addInvitation(@RequestBody InvitationRequest invitation) throws Exception {
        return invitationService.addInvitation(invitation);
    }
}
