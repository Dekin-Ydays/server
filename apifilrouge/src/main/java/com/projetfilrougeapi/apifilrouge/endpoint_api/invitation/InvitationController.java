package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class InvitationController {

    private final InvitationRepository invitationRepository;

    public InvitationController(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @GetMapping("/invitations")
    public CollectionModel<EntityModel<Invitation>> getAllInvitations() {
        List<EntityModel<Invitation>> invitations = invitationRepository.findAll().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getInvitationId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(invitation.getEvent().getEvent_id())).withRel("event")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"));
    }

    @GetMapping("/invitations/{id}")
    public EntityModel<Invitation> getInvitationById(@PathVariable Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(invitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(id)).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(EventController.class).getEventById(invitation.getEvent().getEvent_id())).withRel("event"));
    }

    @GetMapping("/invitation/{id}/events")
    public EntityModel<Event> getEventForInvitation(@PathVariable Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Event event = invitation.getEvent();

        return EntityModel.of(event,
                linkTo(methodOn(InvitationController.class).getEventForInvitation(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withRel("event"),
                linkTo(methodOn(InvitationController.class).getInvitationById(id)).withRel("invitation"));
    }


    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Invitation> addInvitation(@RequestBody Invitation invitation) {
        Invitation savedInvitation = invitationRepository.save(invitation);

        return EntityModel.of(savedInvitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(savedInvitation.getInvitationId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(EventController.class).getEventById(savedInvitation.getEvent().getEvent_id())).withRel("event"));
    }
}
