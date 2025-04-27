package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Invitation>> getAllInvitations() {
        return invitationService.getAllInvitations();
    }

    @GetMapping("/{id}")
    public EntityModel<Invitation> getInvitationById(@PathVariable Long id) {
        return invitationService.getInvitationById(id);
    }

    @GetMapping("/{id}/event")
    public EntityModel<Event> getEventForInvitation(@PathVariable Long id) {
        return invitationService.getEventForInvitation(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Invitation> addInvitation(@RequestBody Invitation invitation) {
        return invitationService.addInvitation(invitation);
    }
}
