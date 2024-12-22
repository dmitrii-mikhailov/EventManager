package org.mikhailov.dm.eventmanager.events;

import jakarta.persistence.EntityNotFoundException;
import org.mikhailov.dm.eventmanager.events.registrations.RegistrationEntity;
import org.mikhailov.dm.eventmanager.events.registrations.RegistrationRepository;
import org.mikhailov.dm.eventmanager.locations.Location;
import org.mikhailov.dm.eventmanager.locations.LocationService;
import org.mikhailov.dm.eventmanager.users.AuthenticationService;
import org.mikhailov.dm.eventmanager.users.User;
import org.mikhailov.dm.eventmanager.users.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final AuthenticationService authenticationService;
    private final EventEntityConverter eventEntityConverter;
    private final LocationService locationService;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository,
                        RegistrationRepository registrationRepository,
                        AuthenticationService authenticationService,
                        EventEntityConverter eventEntityConverter,
                        LocationService locationService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.authenticationService = authenticationService;
        this.eventEntityConverter = eventEntityConverter;
        this.locationService = locationService;
    }

    public Event createNewEvent(EventCreateRequestDto eventCreateRequestDto) {

        if (!locationService.locationExists(eventCreateRequestDto.locationId())) {
            throw new EntityNotFoundException("Error occurred while creating event: Location not found");
        }

        Location location = locationService.getLocation(eventCreateRequestDto.locationId());

        if (location.capacity() < eventCreateRequestDto.maxPlaces()) {
            throw new IllegalArgumentException("Error occurred while creating event: Capacity exceeded");
        }

        User user = authenticationService.getCurrentUser();

        EventEntity eventEntity = new EventEntity(
                null,
                eventCreateRequestDto.name(),
                user.id(),
                eventCreateRequestDto.maxPlaces(),
                List.of(),
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.locationId(),
                EventStatus.WAIT_START.name()
        );
        return eventEntityConverter.toDomain(eventRepository.save(eventEntity));
    }

    public void deleteEvent(long eventId) {
        User currentUser = authenticationService.getCurrentUser();
        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));

        if (!currentUser.role().equals(UserRole.ADMIN) && !Objects.equals(currentUser.id(), event.getOwnerId())) {
            throw new IllegalArgumentException("User %d does not own event %d".formatted(currentUser.id(), eventId));
        }

        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("Event %s cannot be cancelled due to status not being %s".formatted(eventId, EventStatus.WAIT_START));
        }
        eventRepository.updateEventStatus(eventId, EventStatus.CANCELLED.name());
    }

    public Event getEventById(long eventId) {
        EventEntity foundEvent = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));
        return eventEntityConverter.toDomain(foundEvent);
    }

    public Event updateEvent(long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        EventEntity currentEntity = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));

        if (!currentEntity.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("Event %s cannot be updated due to status not being %s".formatted(eventId, EventStatus.WAIT_START));
        }
        User currentUser = authenticationService.getCurrentUser();
        if (!currentUser.role().equals(UserRole.ADMIN) && !Objects.equals(currentUser.id(), currentEntity.getOwnerId())) {
            throw new IllegalArgumentException("User %d does not own event %d".formatted(currentUser.id(), eventId));
        }
        if (eventUpdateRequestDto.maxPlaces() !=null && eventUpdateRequestDto.maxPlaces() < currentEntity.getRegistrationList().size()) {
            throw new IllegalArgumentException("Error occurred while updating event: Max places exceeded");
        }
        if (!locationService.locationExists(eventUpdateRequestDto.locationId())) {
            throw new EntityNotFoundException("Error occurred while updating event: Location not found");
        }
        Location location = locationService.getLocation(eventUpdateRequestDto.locationId());
        if (eventUpdateRequestDto.maxPlaces() != null && location.capacity() < eventUpdateRequestDto.maxPlaces()) {
            throw new IllegalArgumentException("Error occurred while updating event: Location capacity exceeded");
        }
        if (eventUpdateRequestDto.name() != null) {
            currentEntity.setName(eventUpdateRequestDto.name());
        }
        if (eventUpdateRequestDto.maxPlaces() != null) {
            currentEntity.setMaxPlaces(eventUpdateRequestDto.maxPlaces());
        }
        if (eventUpdateRequestDto.date() != null) {
            currentEntity.setDate(eventUpdateRequestDto.date());
        }
        if (eventUpdateRequestDto.cost() != null) {
            currentEntity.setCost(eventUpdateRequestDto.cost());
        }
        if (eventUpdateRequestDto.duration() != null) {
            currentEntity.setDuration(eventUpdateRequestDto.duration());
        }
        if (eventUpdateRequestDto.locationId() != null) {
            currentEntity.setLocationId(eventUpdateRequestDto.locationId());
        }
        return eventEntityConverter.toDomain(eventRepository.save(currentEntity));
    }

    public List<Event> searchEvents(EventSearchRequestDto requestDto) {
        List<EventEntity> eventEntities = eventRepository.searchEvents(
                requestDto.name(),
                requestDto.placesMin(),
                requestDto.placesMax(),
                requestDto.dateStartAfter(),
                requestDto.dateStartBefore(),
                requestDto.costMin(),
                requestDto.costMax(),
                requestDto.durationMin(),
                requestDto.durationMax(),
                requestDto.locationId(),
                requestDto.eventStatus()
        );
        return eventEntities
                .stream()
                .map(eventEntityConverter::toDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents() {
        User currentUser = authenticationService.getCurrentUser();
        List<EventEntity> eventEntities = eventRepository.getEventEntityByOwnerId(currentUser.id());
        return eventEntities
                .stream()
                .map(eventEntityConverter::toDomain)
                .toList();
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
    @Scheduled(fixedRate = 1000*10)
    public void startEvents() {
        log.info("Starting time-appropriate events that are in status {}", EventStatus.WAIT_START);
        eventRepository.startEvents();
    }

    @Scheduled(fixedRate = 1000*10)
    public void finishEvents() {
        log.info("Finishing time-appropriate events that are in status {}", EventStatus.STARTED);
        eventRepository.finishEvents();
    }
}