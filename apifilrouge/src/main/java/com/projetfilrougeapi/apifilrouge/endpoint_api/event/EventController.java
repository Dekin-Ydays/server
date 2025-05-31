package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.DTO.EventRequest;
import com.projetfilrougeapi.apifilrouge.DTO.EventResponse;
import com.projetfilrougeapi.apifilrouge.DTO.ParticipantListRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserSummary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Event>> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<EventResponse> createEvent(@RequestBody EventRequest request) {
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
    public EntityModel<User> getOrganizerForEvent(@PathVariable Long id) {
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

    @PatchMapping("/{id}")
    public EntityModel<EventResponse> patchEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }

    @PostMapping("/{eventId}/participants")
    public EntityModel<EventResponse> addParticipants(@PathVariable Long eventId, @RequestBody ParticipantListRequest request) {
        return eventService.addParticipantsToEvent(eventId, request.getUserIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
