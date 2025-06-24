package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public CollectionModel<EntityModel<Invitation>> getAllInvitations() {
        List<EntityModel<Invitation>> invitations = invitationRepository.findAll().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(invitation.getEvent().getId())).withRel("event")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null,null,null, null, null, null, null, null)).withRel("events"));
    }

    public EntityModel<Invitation> getInvitationById(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(invitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(id)).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(InvitationController.class).getEventForInvitation(id)).withRel("event"));
    }


    public EntityModel<Event> getEventForInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Event event = invitation.getEvent();

        return EntityModel.of(event,
                linkTo(methodOn(InvitationController.class).getEventForInvitation(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(event.getId())).withRel("event"),
                linkTo(methodOn(InvitationController.class).getInvitationById(id)).withRel("invitation"));
    }


    public EntityModel<Invitation> addInvitation(Invitation invitation) {
        Invitation savedInvitation = invitationRepository.save(invitation);

        return EntityModel.of(savedInvitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(savedInvitation.getId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(EventController.class).getEventById(savedInvitation.getEvent().getId())).withRel("event"));
    }

    public EntityModel<Invitation> updateInvitation(Long id, Invitation invitation) {
        Invitation existingInvitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (invitation.getEvent() != null) {
            existingInvitation.setEvent(invitation.getEvent());
        }
        if (invitation.getDescription() != null) {
            existingInvitation.setDescription(invitation.getDescription());
        }
        /*if (invitation.getType() != null) {
            existingInvitation.setType(invitation.getType());
        }*/
        if (invitation.getStatus() != null) {
            existingInvitation.setStatus(invitation.getStatus());
        }

        Invitation updatedInvitation = invitationRepository.save(existingInvitation);

        return EntityModel.of(updatedInvitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(updatedInvitation.getId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(EventController.class).getEventById(updatedInvitation.getEvent().getId())).withRel("event"));
    }
    public void deleteInvitation(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        invitationRepository.delete(invitation);
    }
}
