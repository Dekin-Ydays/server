package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Map;
import java.util.HashMap;

import com.projetfilrougeapi.apifilrouge.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EventController {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public CollectionModel<EntityModel<Event>> getAllEvents() {
        List<EntityModel<Event>> events = eventRepository.findAll().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(EventController.class).getAllEvents()).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"));
    }

    @PostMapping("/events")
    public EntityModel<Event> createEvent(@RequestBody Event event) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        event.setUser(user);

        Event savedEvent = eventRepository.save(event);

        return EntityModel.of(savedEvent,
                linkTo(methodOn(EventController.class).getEventById(savedEvent.getEvent_id())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(savedEvent.getEvent_id())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getEvent_id())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(event.getEvent_id())).withRel("user"));
    }

    @GetMapping("/events/{id}")
    public EntityModel<Event> getEventById(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(event,
                linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getEvent_id())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getEvent_id())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(event.getEvent_id())).withRel("user"));
    }

    @GetMapping("/events/{id}/places")
    public EntityModel<Place> getPlaceForEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Place place = event.getPlace();
        if (place == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun lieu trouvé pour cet événement");
        }

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getPlaceId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(id)).withRel("invitations"));

    }

    @GetMapping("/events/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Invitation>> invitations = event.getInvitations().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getInvitationId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withRel("event"))).collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(EventController.class).getInvitationsForEvent(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withRel("event"));
    }

    @GetMapping("/events/{id}/user")
    public EntityModel<User> getUserForEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = event.getUser();
        user.setPassword("xxx");

        return EntityModel.of(user,
                linkTo(methodOn(EventController.class).getUserForEvent(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"));
    }


}
