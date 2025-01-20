package org.mikhailov.dm.eventmanager.events.registrations;

import org.mikhailov.dm.eventmanager.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final RegistrationService registrationService;
    private final EventDtoConverter eventDtoConverter;

    public RegistrationController(RegistrationService registrationService, EventDtoConverter eventDtoConverter) {
        this.registrationService = registrationService;
        this.eventDtoConverter = eventDtoConverter;
    }


    @PostMapping("/registrations/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> registerToEvent(@PathVariable Long eventId) {
        log.info("Registering to event {}", eventId);
        registrationService.registerToEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/registrations/cancel/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> cancelRegistrationToEvent(@PathVariable Long eventId) {
        log.info("Cancelling registration for event {}", eventId);
        registrationService.cancelRegistration(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/registrations/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getMyRegistrations() {
        log.info("Getting current user registrations");
        List<Event> events = registrationService.getEventsUserRegisteredOn();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events
                        .stream()
                        .map(eventDtoConverter::toDto)
                        .toList());
    }
}
