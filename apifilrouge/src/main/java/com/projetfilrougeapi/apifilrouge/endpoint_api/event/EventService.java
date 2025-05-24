package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.DTO.EventRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
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
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, CategoryRepository categoryRepository, PlaceRepository placeRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.placeRepository = placeRepository;
    }

    public CollectionModel<EntityModel<Event>> getAllEvents() {
        List<EntityModel<Event>> events = eventRepository.findAll().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(EventController.class).getAllEvents()).withSelfRel(),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("invitations"));
    }

    public EntityModel<Event> createEvent(EventRequest request) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        Event event = new Event();
        event.setUser(user);
        event.setDate(request.getDate());
        event.setDescription(request.getDescription());
        event.setName(request.getName());
        event.setAddress(request.getAddress());
        event.setMaxCustomers(request.getMaxCustomers());
        event.setIsTrending(request.getIsTrending());
        event.setStatus(request.getStatus());
        event.setPrice(request.getPrice());

        // Place
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé"));
        event.setPlace(place);

        // Catégories
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            event.setCategories(categories);
        }

        Event savedEvent = eventRepository.save(event);

        return EntityModel.of(savedEvent,
                linkTo(methodOn(EventController.class).getEventById(savedEvent.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(savedEvent.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(savedEvent.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(savedEvent.getId())).withRel("user"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(savedEvent.getId())).withRel("categories"));
    }


    public EntityModel<Event> getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(event,
                linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(event.getId())).withRel("user"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(event.getId())).withRel("categories"));
    }

    public EntityModel<Place> getPlaceForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Place place = event.getPlace();
        if (place == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun lieu trouvé pour cet événement");
        }

        return EntityModel.of(place,
                linkTo(methodOn(PlaceController.class).getPlaceById(place.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"),
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"));
    }

    public CollectionModel<Category> getCategoriesForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Événement non trouvé avec l'ID: " + eventId));

        List<Category> categories = event.getCategories();

        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune catégorie associée à cet événement");
        }
        return CollectionModel.of(categories,
                linkTo(methodOn(EventController.class).getEventById(eventId)).withRel("event"));
    }


    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Invitation>> invitations = event.getInvitations().stream()
                .map(invitation -> EntityModel.of(invitation,
                        linkTo(methodOn(InvitationController.class).getInvitationById(invitation.getId())).withSelfRel(),
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withRel("event")))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(EventController.class).getInvitationsForEvent(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(event.getId())).withRel("event"));
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

    public EntityModel<Event> updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getDate() != null) event.setDate(request.getDate());
        if (request.getAddress() != null) event.setAddress(request.getAddress());
        if (request.getMaxCustomers() != null) event.setMaxCustomers(request.getMaxCustomers());
        if (request.getIsTrending() != null) event.setIsTrending(request.getIsTrending());
        if (request.getStatus() != null) event.setStatus(request.getStatus());

        // place
        if (request.getPlaceId() != null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé"));
            event.setPlace(place);
        }

        // catégories
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une ou plusieurs catégories sont invalides.");
            }
            event.setCategories(categories);
        }

        Event updatedEvent = eventRepository.save(event);

        return EntityModel.of(updatedEvent,
                linkTo(methodOn(EventController.class).getEventById(updatedEvent.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(updatedEvent.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(updatedEvent.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getUserForEvent(updatedEvent.getId())).withRel("user"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(updatedEvent.getId())).withRel("categories"));
    }


    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        eventRepository.delete(event);
    }
}
