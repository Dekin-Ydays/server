package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.user.User;
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
    public EntityModel<Event> createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @GetMapping("/{id}")
    public EntityModel<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/places")
    public EntityModel<Place> getPlaceForEvent(@PathVariable Long id) {
        return eventService.getPlaceForEvent(id);
    }

    @GetMapping("/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForEvent(@PathVariable Long id) {
        return eventService.getInvitationsForEvent(id);
    }

    @GetMapping("/{id}/user")
    public EntityModel<User> getUserForEvent(@PathVariable Long id) {
        return eventService.getUserForEvent(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

}
