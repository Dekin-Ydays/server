package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.Specification.EventSpecification;
import com.projetfilrougeapi.apifilrouge.assembler.EventSummaryResponseAssembler;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.CityRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;
    private final PagedResourcesAssembler pagedResourcesAssembler;
    private final EventSummaryResponseAssembler eventSummaryResponseAssembler;

    public EventService(EventRepository eventRepository, UserRepository userRepository, CategoryRepository categoryRepository, PlaceRepository placeRepository, CityRepository cityRepository, PagedResourcesAssembler pagedResourcesAssembler, EventSummaryResponseAssembler eventSummaryResponseAssembler) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.placeRepository = placeRepository;
        this.cityRepository = cityRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.eventSummaryResponseAssembler = eventSummaryResponseAssembler;
    }

    public CollectionModel<EntityModel<EventSummaryResponse>> getAllEvents(Pageable pageable, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate, String[] categories, String[] cities, String[] places) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Specification<Event> spec = Specification.where(null);

        if (minPrice != null && maxPrice != null) {
            spec = spec.and(EventSpecification.hasPriceBetween(minPrice, maxPrice));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(EventSpecification.hasDateBetween(startDate, endDate));
        }

        if (categories != null && categories.length > 0) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        if (cities != null && cities.length > 0) {
            spec = spec.and(EventSpecification.hasCityNames(cities));
        }

        if (places != null && places.length > 0) {
            spec = spec.and(EventSpecification.hasPlaceNames(places));
        }
        // transforme en EventResponse
        Page<Event> events = eventRepository.findAll(spec, pageable);

        // 3. On convertit la Page<Event> en une Page<EventSummaryResponse>.
        Page<EventSummaryResponse> eventsDto = events.map(EventSummaryResponse::fromEntity);

        return pagedResourcesAssembler.toModel(eventsDto, eventSummaryResponseAssembler);
    }

    public EntityModel<EventResponse> createEvent(EventRequest request) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User organizer = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        Event event = new Event();
        event.setOrganizer(organizer);
        event.setDate(request.getDate());
        event.setDescription(request.getDescription());
        event.setName(request.getName());
        event.setAddress(request.getAddress());
        event.setMaxCustomers(request.getMaxCustomers());
        event.setIsTrending(request.getIsTrending());
        event.setStatus(request.getStatus());
        event.setPrice(request.getPrice());
        event.setContentHtml(request.getContentHtml());
        event.setImageUrl(request.getImageUrl());

        boolean alreadyHasEventsInCity = eventRepository.existsByOrganizerIdAndCityId(
                organizer.getId(),
                request.getCityId()
        );
        // first_edition
        if (alreadyHasEventsInCity) {
            event.setIsFirstEdition(false);
        } else {
            event.setIsFirstEdition(true);
        }
        // Place
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé"));
        event.setPlace(place);

        // City
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville non trouvé"));
        event.setCity(city);

        // Catégories
        if (request.getCategoryKeys() != null && !request.getCategoryKeys().isEmpty()) {
            List<Category> categories = categoryRepository.findByKeyIn(request.getCategoryKeys());

            if (categories.size() != request.getCategoryKeys().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une ou plusieurs clés de catégorie sont invalides.");
            }
            event.setCategories(new HashSet<>(categories));
        }

        Event savedEvent = eventRepository.save(event);

        // Transformation en EventResponse
        EventResponse response = EventResponse.fromEntity(savedEvent);

        return EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEventById(savedEvent.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null, null, null, null, null)).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(savedEvent.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getCityForEvent(savedEvent.getId())).withRel("city"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(savedEvent.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getOrganizerForEvent(savedEvent.getId())).withRel("organizer"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(savedEvent.getId())).withRel("categories"),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(savedEvent.getId())).withRel("participants"));
    }

    /**
     * Retrieves a list of "first edition" events with a fixed limit,
     * The results are shuffled to provide a random order.
     *
     * @param city  Optional name of the city to filter results.
     * @param place Optional name of the place to filter results.
     * @param limit The maximum number of events to return.
     * @return A CollectionModel of EventSummaryResponse DTOs.
     */
    public CollectionModel<EntityModel<EventSummaryResponse>> getFirstEditionEvents(String city, String place, int limit) {
        Specification<Event> spec = EventSpecification.isFirstEdition();

        if (city != null && !city.isEmpty()) {
            spec = spec.and(EventSpecification.hasCityNames(new String[]{city}));
        }
        if (place != null && !place.isEmpty()) {
            spec = spec.and(EventSpecification.hasPlaceNames(new String[]{place}));
        }

        // L'optimisation @EntityGraph garantit que cela se fait en une seule requête.
        List<Event> allMatchingEvents = eventRepository.findAll(spec);

        // random shuffle
        Collections.shuffle(allMatchingEvents);

        // Keep the limit
        List<EntityModel<EventSummaryResponse>> eventModels = allMatchingEvents.stream()
                .limit(limit)
                .map(EventSummaryResponse::fromEntity)
                .map(eventSummaryResponseAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(eventModels,
                linkTo(methodOn(EventController.class).getFirstEditionEvents(city, place, limit)).withSelfRel());
    }

    // ADD MULTIPLE PARTICIPANTS TO EVENT

    public EntityModel<EventSummaryResponse> addParticipantsToEvent(Long eventId, List<Long> userIds) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        List<User> usersToAdd = userRepository.findAllById(userIds);

        List<User> newParticipants = usersToAdd.stream()
                .filter(user -> !event.getParticipants().contains(user))
                .toList();

        int totalAfterAdd = event.getParticipants().size() + newParticipants.size();
        if (event.getMaxCustomers() != null && totalAfterAdd > event.getMaxCustomers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max participants reached");
        }

        event.getParticipants().addAll(newParticipants);
        Event savedEvent = eventRepository.save(event);

        EventSummaryResponse response = EventSummaryResponse.fromEntity(savedEvent);

        return EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEventById(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(eventId)).withRel("participants"));
    }
    // ADD 1 PARTICIPANT TO EVENT
    public EntityModel<EventSummaryResponse> addParticipantToEvent(Long eventId, Long userIds) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        User userToAdd = userRepository.findById(userIds)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int totalAfterAdd = event.getParticipants().size() + 1;
        if (event.getMaxCustomers() != null && totalAfterAdd > event.getMaxCustomers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max participants reached");
        }

        event.getParticipants().add(userToAdd);
        Event savedEvent = eventRepository.save(event);

        EventSummaryResponse response = EventSummaryResponse.fromEntity(savedEvent);

        return EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEventById(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(eventId)).withRel("participants"));
    }

    public EntityModel<EventSummaryResponse> removeParticipantsFromEvent(Long eventId, List<Long> userIds) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evénement introuvable"));

            List<User> usersToRemove = userRepository.findAllById(userIds);
            event.getParticipants().removeAll(usersToRemove);

            Event updatedEvent = eventRepository.save(event);

            return EntityModel.of(EventSummaryResponse.fromEntity(updatedEvent),
                    linkTo(methodOn(EventController.class).getEventById(eventId)).withSelfRel(),
                    linkTo(methodOn(EventController.class).getParticipantsForEvent(eventId)).withRel("participants"));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur est survenue lors de la suppression des participants ", e);
        }
    }


    public EntityModel<EventResponse> getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EventResponse response = EventResponse.fromEntity(event);

        return EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null, null, null, null, null)).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(event.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getCityForEvent(event.getId())).withRel("city"),
                linkTo(methodOn(EventController.class).getOrganizerForEvent(event.getId())).withRel("organizer"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(event.getId())).withRel("categories"),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(event.getId())).withRel("participants"));
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
                linkTo(methodOn(PlaceController.class).findPlaces(null)).withRel("places"));
    }

    public EntityModel<City> getCityForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        City city = event.getCity();
        if (city == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun lieu trouvé pour cet événement");
        }

        return EntityModel.of(city,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"),
                linkTo(methodOn(CityController.class).findCities(null, null)).withRel("cities"));
    }

    public CollectionModel<Category> getCategoriesForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Événement non trouvé avec l'ID: " + eventId));

        List<Category> categories = new ArrayList<>(event.getCategories());
        ;

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

    public EntityModel<UserResponse> getOrganizerForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        User organizer = event.getOrganizer();
        if (organizer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No organizer found for this event");
        }

        UserResponse response = UserResponse.fromEntity(organizer);
        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUserById(organizer.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(id)).withRel("event"));
    }

    public CollectionModel<EntityModel<UserSummary>> getParticipantsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<EntityModel<UserSummary>> participants = event.getParticipants().stream()
                .map(user -> {
                    UserSummary summary = new UserSummary(user.getId(), user.getPseudo(), user.getImageUrl());
                    return EntityModel.of(summary,
                            linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel()
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(participants,
                linkTo(methodOn(EventController.class).getParticipantsForEvent(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(eventId)).withRel("event"));
    }


    public EntityModel<EventResponse> updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getDate() != null) event.setDate(request.getDate());
        if (request.getAddress() != null) event.setAddress(request.getAddress());
        if (request.getMaxCustomers() != null) event.setMaxCustomers(request.getMaxCustomers());
        if (request.getIsTrending() != null) event.setIsTrending(request.getIsTrending());
        if (request.getStatus() != null) event.setStatus(request.getStatus());
        if (request.getContentHtml() != null) event.setContentHtml(request.getContentHtml());
        if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());

        // place
        if (request.getPlaceId() != null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieu non trouvé"));
            event.setPlace(place);
        }

        // City
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville non trouvé"));
            event.setCity(city);
        }

        // catégories
        if (request.getCategoryKeys() != null && !request.getCategoryKeys().isEmpty()) {
            List<Category> categories = categoryRepository.findByKeyIn(request.getCategoryKeys());

            if (categories.size() != request.getCategoryKeys().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or multiple keys of categories are invalids.");
            }

            event.setCategories(new HashSet<>(categories));
        }

        // participants
        if (request.getParticipantIds() != null) {
            List<User> participants = userRepository.findAllById(request.getParticipantIds());
            if (participants.size() != request.getParticipantIds().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certains participants sont invalides.");
            }
            if (event.getMaxCustomers() != null && participants.size() > event.getMaxCustomers()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de participants dépasse la limite maximum de l'événement.");
            }
            event.setParticipants(participants);
        }

        Event updatedEvent = eventRepository.save(event);

        EventResponse response = EventResponse.fromEntity(updatedEvent);

        return EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEventById(updatedEvent.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null, null, null, null, null)).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(updatedEvent.getId())).withRel("places"),
                linkTo(methodOn(EventController.class).getCityForEvent(updatedEvent.getId())).withRel("city"),
                linkTo(methodOn(EventController.class).getInvitationsForEvent(updatedEvent.getId())).withRel("invitations"),
                linkTo(methodOn(EventController.class).getOrganizerForEvent(updatedEvent.getId())).withRel("organizer"),
                linkTo(methodOn(EventController.class).getCategoriesForEvent(updatedEvent.getId())).withRel("categories"),
                linkTo(methodOn(EventController.class).getParticipantsForEvent(updatedEvent.getId())).withRel("participants"));
    }

    @Transactional
    public EntityModel<EventResponse> cancelEvent(Long id) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found"));

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));

            boolean isOrganizer = event.getOrganizer().equals(currentUser);

            if (isAdmin || isOrganizer) {
                event.setStatus(EventStatus.CANCELLED);
            }

            Event updatedEvent = eventRepository.save(event);
            EventResponse response = EventResponse.fromEntity(updatedEvent);

            return EntityModel.of(response,
                    linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while canceling the event.", e);
        }
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        eventRepository.delete(event);
    }
}
