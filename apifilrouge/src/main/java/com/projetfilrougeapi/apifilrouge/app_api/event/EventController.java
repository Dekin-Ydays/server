package com.projetfilrougeapi.apifilrouge.app_api.event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/actives")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllActiveEvents());
    }

    // Un utilisateur rejoint un événement
//    @PostMapping("/{eventId}/join/{userId}")
//    public ResponseEntity<Event> joinEvent(@PathVariable Integer eventId, @PathVariable Integer userId) {
//        return ResponseEntity.ok(eventService.addParticipant(eventId, userId));
//    }
}
