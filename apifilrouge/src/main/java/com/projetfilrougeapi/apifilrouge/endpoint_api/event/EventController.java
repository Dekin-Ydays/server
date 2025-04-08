package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Add your endpoint methods here
    // For example, to get all events:
     @GetMapping("/events")
     public List<Event> getAllEvents() {

        return eventRepository.findAll();
     }
    // Example of a method to create an event
     @PostMapping("/events")
     public Event createEvent(@RequestBody Event event) {
         return eventRepository.save(event);
     }
    // Example of a method to get an event by ID
     @GetMapping("/events/{id}")
     public ResponseEntity<Event> getEventById(@PathVariable Long id) {
         return eventRepository.findById(id)
                 .map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
}
