package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EventController {
    @Autowired
    private final EventRepository eventRepository;

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
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"));
    }

@PostMapping("/events")
public EntityModel<Event> createEvent(@RequestBody Event event) {
    Event savedEvent = eventRepository.save(event);

    return EntityModel.of(savedEvent,
            linkTo(methodOn(EventController.class).getEventById(savedEvent.getEvent_id())).withSelfRel(),
            linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
            linkTo(methodOn(EventController.class).getPlaceForEvent(savedEvent.getEvent_id())).withRel("places"));
}

    @GetMapping("/events/{id}")
    public EntityModel<Event> getEventById(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(event,
                linkTo(methodOn(EventController.class).getEventById(id)).withSelfRel(),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(EventController.class).getPlaceForEvent(event.getEvent_id())).withRel("places"));
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
                linkTo(methodOn(PlaceController.class).getAllPlaces()).withRel("places"));
    }



}
