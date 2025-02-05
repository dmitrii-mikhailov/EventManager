package org.mikhailov.dm.eventmanager.events;

import org.mikhailov.dm.eventmanager.events.registrations.Registration;
import org.springframework.stereotype.Component;

@Component
public class EventEntityConverter {
    public Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getMaxPlaces(),
                entity.getRegistrationList()
                        .stream()
                        .map(en -> new Registration(
                                en.getId(),
                                en.getUserId(),
                                entity.getId()
                        ))
                        .toList(),
                entity.getDate(),
                entity.getCost(),
                entity.getDuration(),
                entity.getLocationId(),
                entity.getStatus()
        );
    }
}