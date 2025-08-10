package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.projetfilrougeapi.apifilrouge.email.EmailSender;
import org.springframework.stereotype.Service;

@Service
public class EventEmailUpdateManager {
    private final EventRepository eventRepository;

    public EventEmailUpdateManager( EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void sendMultipleMailToParticipants(Event event) {
        event.getParticipants().forEach(participant -> {
            EmailSender emailSender = new EmailSender();
            try {
                emailSender.sendIUpdateEventEmail(participant, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
