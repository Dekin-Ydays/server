package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.user.User;
import com.projetfilrougeapi.apifilrouge.user.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

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

    public EntityModel<Event> createEvent(Event event) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        event.setUser(user);
        Event savedEvent = eventRepository.save(event);

        return EntityModel.of(savedEvent,
                linkTo(methodOn(EventController.class).getEventById(savedEvent.getEvent_id())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(savedEvent.getEvent_id())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(savedEvent.getEvent_id())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(savedEvent.getEvent_id())).withRel("user"));
    }

    public EntityModel<Event> getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(event,
                linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getEvent_id())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getEvent_id())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(event.getEvent_id())).withRel("user"));
    }

    public EntityModel<Place> getPlaceForEvent(Long id) {
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

    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Invitation>> invitations = event.getInvitations().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getInvitationId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withRel("event")))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(EventController.class).getInvitationsForEvent(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withRel("event"));
    }

    public EntityModel<User> getUserForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = event.getUser();
        user.setPassword("xxx");

        return EntityModel.of(user,
                linkTo(methodOn(EventController.class).getUserForEvent(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"));
    }
    public EntityModel<Event> updateEvent(Long id, Event event) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Check if the event name is not null before updating
        if (event.getEventName() != null) {
            existingEvent.setEventName(event.getEventName());
        }
        if (event.getDescription() != null) {
            existingEvent.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            existingEvent.setEventDate(event.getEventDate());
        }
        if (event.getAddress() != null) {
            existingEvent.setAddress(event.getAddress());
        }
        if (event.getMaxCustomers() != null) {
            existingEvent.setMaxCustomers(event.getMaxCustomers());
        }
        if (event.getIsTrending() != null) {
            existingEvent.setIsTrending(event.getIsTrending());
        }
        if (event.getActive() != null) {
            existingEvent.setActive(event.getActive());
        }
        if (event.getPlace() != null) {
            existingEvent.setPlace(event.getPlace());
        }
        if (event.getUser() != null) {
            existingEvent.setUser(event.getUser());
        }

        Event updatedEvent = eventRepository.save(existingEvent);

        return EntityModel.of(updatedEvent,
                linkTo(methodOn(EventController.class).getEventById(updatedEvent.getEvent_id())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(updatedEvent.getEvent_id())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(updatedEvent.getEvent_id())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(updatedEvent.getEvent_id())).withRel("user"));
    }
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        eventRepository.delete(event);
    }
}
