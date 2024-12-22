package org.mikhailov.dm.eventmanager.events;

import jakarta.validation.Valid;
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
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final EventDtoConverter eventDtoConverter;

    public EventController(EventService eventService, EventDtoConverter eventDtoConverter) {
        this.eventService = eventService;
        this.eventDtoConverter = eventDtoConverter;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventDto> createNewEvent(@RequestBody @Valid EventCreateRequestDto eventCreateRequestDto) {
        log.info("Creating new event {}", eventCreateRequestDto);
        Event newEvent = eventService.createNewEvent(eventCreateRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventDtoConverter.toDto(newEvent));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteEventById(@PathVariable long eventId) {
        log.info("Deleting event {}", eventId);
        eventService.deleteEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<EventDto> getEventById(@PathVariable long eventId) {
        log.info("Getting event {}", eventId);
        Event foundEvent = eventService.getEventById(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoConverter.toDto(foundEvent));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<EventDto> updateEventById(
            @PathVariable long eventId,
            @RequestBody @Valid EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("Updating event {}", eventId);
        Event updatedEvent = eventService.updateEvent(eventId, eventUpdateRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoConverter.toDto(updatedEvent));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchRequestDto requestDto) {
        log.info("Searching events for {}", requestDto);
        List<Event> events = eventService.searchEvents(requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events
                        .stream()
                        .map(eventDtoConverter::toDto)
                        .toList());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getMyEvents(@RequestHeader("Authorization") String token) {
        log.info("Getting my events from {}", token);
        List<Event> events = eventService.getCurrentUserEvents();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events
                        .stream()
                        .map(eventDtoConverter::toDto)
                        .toList());
    }

    @PostMapping("/registrations/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> registerToEvent(@PathVariable Long eventId) {
        log.info("Registering to event {}", eventId);
        eventService.registerToEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/registrations/cancel/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> cancelRegistrationToEvent(@PathVariable Long eventId) {
        log.info("Cancelling registration for event {}", eventId);
        eventService.cancelRegistration(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/registrations/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getMyRegistrations() {
        log.info("Getting current user registrations");
        List<Event> events = eventService.getEventsUserRegisteredOn();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events
                        .stream()
                        .map(eventDtoConverter::toDto)
                        .toList());
    }
}