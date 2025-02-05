package org.mikhailov.dm.eventmanager.events.registrations;

import jakarta.persistence.EntityNotFoundException;
import org.mikhailov.dm.eventmanager.events.*;
import org.mikhailov.dm.eventmanager.users.AuthenticationService;
import org.mikhailov.dm.eventmanager.users.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final AuthenticationService authenticationService;
    private final EventEntityConverter eventEntityConverter;

    public RegistrationService(EventRepository eventRepository, RegistrationRepository registrationRepository, AuthenticationService authenticationService, EventEntityConverter eventEntityConverter) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.authenticationService = authenticationService;
        this.eventEntityConverter = eventEntityConverter;
    }

    public void registerToEvent(Long eventId) {
        EventEntity currentEntity = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));
        if (!(currentEntity.getStatus().equals(EventStatus.WAIT_START.name()))) {
            throw new IllegalArgumentException("Event with id %d is not waiting start".formatted(eventId));
        }
        User currentUser = authenticationService.getCurrentUser();
        RegistrationEntity registrationEntity = new RegistrationEntity(
                null,
                currentUser.id(),
                currentEntity
        );
        registrationRepository.save(registrationEntity);
    }

    public void cancelRegistration(Long eventId) {
        User currentUser = authenticationService.getCurrentUser();
        RegistrationEntity registration = registrationRepository
                .findByEventId(eventId);
        if (!currentUser.id().equals(registration.getUserId())) {
            throw new IllegalArgumentException("Can't cancel other User's registration");
        }
        EventEntity event = registration.getEvent();
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("Event with id %d is not waiting start".formatted(eventId));
        }
        registrationRepository.delete(registration);
    }

    public List<Event> getEventsUserRegisteredOn() {
        User currentUser = authenticationService.getCurrentUser();
        List<EventEntity> events = eventRepository.getEventsByUserRegistrationId(currentUser.id());
        return events
                .stream()
                .map(eventEntityConverter::toDomain)
                .toList();
    }
}
