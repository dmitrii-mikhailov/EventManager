package org.mikhailov.dm.eventmanager.events;

import org.springframework.stereotype.Component;

@Component
public class EventDtoConverter {
    public EventDto toDto(Event event){
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.registrationList().size(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }
}