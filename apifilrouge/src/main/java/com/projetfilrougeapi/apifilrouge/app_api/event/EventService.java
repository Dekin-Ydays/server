package com.projetfilrougeapi.apifilrouge.app_api.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getAllActiveEvents() {
        return eventRepository.findByActiveTrue();
    }
}
