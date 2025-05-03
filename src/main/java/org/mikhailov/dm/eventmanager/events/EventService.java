package org.mikhailov.dm.eventmanager.events;

import jakarta.persistence.EntityNotFoundException;
import org.mikhailov.dm.eventmanager.events.registrations.RegistrationEntity;
import org.mikhailov.dm.eventmanager.kafkaevent.EventChangeKafkaMessage;
import org.mikhailov.dm.eventmanager.kafkaevent.EventFieldChange;
import org.mikhailov.dm.eventmanager.kafkaevent.EventKafkaEventSender;
import org.mikhailov.dm.eventmanager.locations.Location;
import org.mikhailov.dm.eventmanager.locations.LocationService;
import org.mikhailov.dm.eventmanager.users.AuthenticationService;
import org.mikhailov.dm.eventmanager.users.User;
import org.mikhailov.dm.eventmanager.users.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final EventEntityConverter eventEntityConverter;
    private final LocationService locationService;
    private final EventKafkaEventSender eventKafkaEventSender;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository,
                        AuthenticationService authenticationService,
                        EventEntityConverter eventEntityConverter,
                        LocationService locationService, EventKafkaEventSender eventKafkaEventSender) {
        this.eventRepository = eventRepository;
        this.authenticationService = authenticationService;
        this.eventEntityConverter = eventEntityConverter;
        this.locationService = locationService;
        this.eventKafkaEventSender = eventKafkaEventSender;
    }

    public Event createNewEvent(Event event) {
        checkLocationExists(event);
        Location location = locationService.getLocation(event.locationId());
        checkLocationCapacity(location, event);
        User user = authenticationService.getCurrentUser();
        EventEntity eventEntity = new EventEntity(
                null,
                event.name(),
                user.id(),
                event.maxPlaces(),
                List.of(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                EventStatus.WAIT_START.name()
        );

        return eventEntityConverter.toDomain(eventRepository.save(eventEntity));
    }

    public void deleteEvent(long eventId) {
        User currentUser = authenticationService.getCurrentUser();
        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));
        checkUserOwnsEvent(currentUser, event);
        checkEventStatus(event, EventStatus.WAIT_START);
        eventRepository.updateEventStatus(eventId, EventStatus.CANCELLED.name());

        eventKafkaEventSender.sendEventChange(new EventChangeKafkaMessage(
                event.getId(),
                currentUser.id(),
                event.getOwnerId(),
                null,
                null,
                null,
                null,
                null,
                null,
                new EventFieldChange<>(EventStatus.WAIT_START.name(), EventStatus.CANCELLED.name()),
                null
        ));
    }

    public Event getEventById(long eventId) {
        EventEntity foundEvent = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));
        return eventEntityConverter.toDomain(foundEvent);
    }

    public Event updateEvent(long eventId, Event event) {
        EventEntity currentEntity = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d is not found".formatted(eventId)));
        checkEventStatus(currentEntity, EventStatus.WAIT_START);
        User currentUser = authenticationService.getCurrentUser();
        checkUserOwnsEvent(currentUser, currentEntity);
        checkRegisteredPlaces(event, currentEntity);

        if (event.locationId() != null) {
            checkLocationExists(event);
            Location location = locationService.getLocation(event.locationId());
            checkLocationCapacity(location, event);
        }

        EventChangeKafkaMessage msg = new EventChangeKafkaMessage(
                eventId,
                currentUser.id(),
                currentEntity.getOwnerId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                currentEntity.getRegistrationList().stream().map(RegistrationEntity::getId).toList()
                );

        if (event.name() != null) {
            msg.setName(new EventFieldChange<>(currentEntity.getName(), event.name()));
            currentEntity.setName(event.name());
        }
        if (event.maxPlaces() != null) {
            msg.setMaxPlaces(new EventFieldChange<>(currentEntity.getMaxPlaces(), event.maxPlaces()));
            currentEntity.setMaxPlaces(event.maxPlaces());
        }
        if (event.date() != null) {
            msg.setDate(new EventFieldChange<>(currentEntity.getDate(), event.date()));
            currentEntity.setDate(event.date());
        }
        if (event.cost() != null) {
            msg.setCost(new EventFieldChange<>(currentEntity.getCost(), event.cost()));
            currentEntity.setCost(event.cost());
        }
        if (event.duration() != null) {
            msg.setDuration(new EventFieldChange<>(currentEntity.getDuration(), event.duration()));
            currentEntity.setDuration(event.duration());
        }
        if (event.locationId() != null) {
            msg.setLocationId(new EventFieldChange<>(currentEntity.getLocationId(), event.locationId()));
            currentEntity.setLocationId(event.locationId());
        }
        eventKafkaEventSender.sendEventChange(msg);
        return eventEntityConverter.toDomain(eventRepository.save(currentEntity));
    }

    public List<Event> searchEvents(EventSearchRequest request) {
        List<EventEntity> eventEntities = eventRepository.searchEvents(
                request.name(),
                request.placesMin(),
                request.placesMax(),
                request.dateStartAfter(),
                request.dateStartBefore(),
                request.costMin(),
                request.costMax(),
                request.durationMin(),
                request.durationMax(),
                request.locationId(),
                request.eventStatus()
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

    private void checkLocationExists(Event event) {
        if (!locationService.locationExists(event.locationId())) {
            throw new EntityNotFoundException("Error occurred while creating event: Location not found");
        }
    }

    private void checkLocationCapacity(Location location, Event event) {
        if (event.maxPlaces() != null && location.capacity() < event.maxPlaces()) {
            throw new IllegalArgumentException("Error occurred while updating event: Location capacity exceeded");
        }
    }

    private void checkUserOwnsEvent(User user, EventEntity event) {
        if (!user.role().equals(UserRole.ADMIN) && !Objects.equals(user.id(), event.getOwnerId())) {
            throw new IllegalArgumentException("User %d does not own event %d".formatted(user.id(), event.getId()));
        }
    }

    private void checkEventStatus(EventEntity event, EventStatus status) {
        if (!event.getStatus().equals(status.name())) {
            throw new IllegalArgumentException("Event %s cannot be cancelled due to status not being %s".formatted(event.getId(), status));
        }
    }

    private void checkRegisteredPlaces(Event event, EventEntity eventEntity) {
        if (event.maxPlaces() !=null && event.maxPlaces() < eventEntity.getRegistrationList().size()) {
            throw new IllegalArgumentException("Error occurred while updating event: Max places exceeded");
        }
    }
}