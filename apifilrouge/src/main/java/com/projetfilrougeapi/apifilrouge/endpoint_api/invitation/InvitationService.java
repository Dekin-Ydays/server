package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.DTO.InvitationRequest;
import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
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
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    public InvitationService(InvitationRepository invitationRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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

    // création d'une invitation
    public EntityModel<Invitation> addInvitation(InvitationRequest invitation) throws Exception {
        Event event = eventRepository.findById(invitation.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        User user = userRepository.findById(invitation.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        User receiver = userRepository.findById(event.getOrganizer().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        Invitation newInvitation = Invitation.builder()
                .event(event)
                .user(user)
                .status(invitation.getStatus())
                .description(invitation.getDescription())
                .build();

        Invitation savedInvitation = invitationRepository.save(newInvitation);

        EmailSender emailSender = new EmailSender("marchalquentin06@gmail.com");
        emailSender.sendInvitationEmail(user,receiver,event);

        return EntityModel.of(savedInvitation,
                linkTo(methodOn(InvitationController.class).getInvitationById(savedInvitation.getId())).withSelfRel(),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"),
                linkTo(methodOn(EventController.class).getEventById(savedInvitation.getEvent().getId())).withRel("event"));
    }

    public EntityModel<Invitation> updateInvitation(Long id, InvitationRequest invitation) {
        Invitation existingInvitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (invitation.getEventId() != null) {
            Event event = eventRepository.findById(invitation.getEventId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
            existingInvitation.setEvent(event);
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
