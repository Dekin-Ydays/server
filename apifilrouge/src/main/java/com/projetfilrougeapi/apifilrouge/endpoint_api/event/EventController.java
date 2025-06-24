package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Retourne une liste d"évenements avec des filtres optionnels. Si aucun paramètre n'est fourni, renvoie tous les événements sans filtre.
    @GetMapping
    public CollectionModel<EntityModel<EventSummaryResponse>> getAllEvents(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String[] categories,
            @RequestParam(required = false) String[] cities,
            @RequestParam(required = false) String[] places


    ) {
        return eventService.getAllEvents(pageable, minPrice, maxPrice, startDate, endDate, categories, cities, places);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/{id}")
    public EntityModel<EventResponse> getEventById(@PathVariable("id") Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/place")
    public EntityModel<Place> getPlaceForEvent(@PathVariable Long id) {
        return eventService.getPlaceForEvent(id);
    }

    @GetMapping("/{id}/city")
    public EntityModel<City> getCityForEvent(@PathVariable Long id) {
        return eventService.getCityForEvent(id);
    }


    @GetMapping("/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(@PathVariable Long id) {
        return eventService.getInvitationsForEvent(id);
    }

    @GetMapping("/{id}/organizer")
    public EntityModel<UserResponse> getOrganizerForEvent(@PathVariable Long id) {
        return eventService.getOrganizerForEvent(id);
    }

    @GetMapping("/{eventId}/categories")
    public CollectionModel<Category> getCategoriesForEvent(@PathVariable Long eventId) {
        return eventService.getCategoriesForEvent(eventId);
    }

    @GetMapping("/{eventId}/participants")
    public CollectionModel<EntityModel<UserSummary>> getParticipantsForEvent(@PathVariable Long eventId) {
        return eventService.getParticipantsForEvent(eventId);
    }


    /**
     * It accepts a 'limit' parameter to define the number of results.
     * @param city The name of the city to filter the results.
     * @param limit The number of events to display (default is 10).
     * @return A collection of events.
     */
    @GetMapping("/first-editions")
    public CollectionModel<EntityModel<EventSummaryResponse>> getFirstEditionEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String place,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return eventService.getFirstEditionEvents(city,place, limit);
    }

    @PatchMapping("/{id}")
    public EntityModel<EventResponse> patchEvent(@PathVariable("id") Long id, @Valid @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }
    // Adds multiple participants to an event.
    @PostMapping("/{eventId}/participants")
    public EntityModel<EventSummaryResponse> addParticipants(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantListRequest request) {
        return eventService.addParticipantsToEvent(eventId, request.getUserIds());
    }

    // Adds a single participant to an event.
    @PostMapping("/{eventId}/participant")
    public EntityModel<EventSummaryResponse> addParticipant(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantRequest request) {
        return eventService.addParticipantToEvent(eventId, request.getUserId());
    }

    @DeleteMapping("/{eventId}/participants")
    public EntityModel<EventSummaryResponse> removeParticipants(@PathVariable("id") Long eventId, @Valid @RequestBody ParticipantListRequest request) {
        return eventService.removeParticipantsFromEvent(eventId, request.getUserIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<EventResponse> cancelEvent(@PathVariable("id") Long id) {
        return eventService.cancelEvent(id);
    }
}
