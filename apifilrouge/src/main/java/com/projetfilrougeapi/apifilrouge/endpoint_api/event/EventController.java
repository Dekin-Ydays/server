package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EventController {
    @Autowired
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream().map(event -> {
            Place place = event.getPlace();
            event.setPlace(new Place(place.getPlaceId(), place.getCity_name(), null));

            event.getPlace().setPlaceId(place.getPlaceId());

            return event;
        }).collect(Collectors.toList());
    }

    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).map(event -> {
            Place place = event.getPlace();
            event.setPlace(new Place(place.getPlaceId(), place.getCity_name(), null));
            event.getPlace().setPlaceId(place.getPlaceId());

            return ResponseEntity.ok(event);
        }).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/events/place/{placeId}")
    public List<Event> getEventsByPlaceId(@PathVariable Long placeId) {
        List<Event> events = eventRepository.findByPlace_PlaceId(placeId);

        return events.stream().map(event -> {
            Place place = event.getPlace();
            place.setEvents(null);

            return event;
        }).collect(Collectors.toList());
    }



}
