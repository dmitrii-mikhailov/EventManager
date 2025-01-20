package org.mikhailov.dm.eventmanager.events;

import org.springframework.stereotype.Component;

import java.util.List;

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

    public Event fromEventCreateRequestDto(EventCreateRequestDto eventCreateRequestDto){
        return new Event(null,
                eventCreateRequestDto.name(),
                null,
                eventCreateRequestDto.maxPlaces(),
                null,
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.locationId(),
                null);
    }

    public Event fromEventUpdateRequestDto(EventUpdateRequestDto eventUpdateRequestDto){
        return new Event(null,
                eventUpdateRequestDto.name(),
                null,
                eventUpdateRequestDto.maxPlaces(),
                null,
                eventUpdateRequestDto.date(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.locationId(),
                null);
    }

    public EventSearchRequest fromEventSearchRequestDto(EventSearchRequestDto eventSearchRequestDto){
        return new EventSearchRequest(
                eventSearchRequestDto.name(),
                eventSearchRequestDto.placesMin(),
                eventSearchRequestDto.placesMax(),
                eventSearchRequestDto.dateStartAfter(),
                eventSearchRequestDto.dateStartBefore(),
                eventSearchRequestDto.costMin(),
                eventSearchRequestDto.costMax(),
                eventSearchRequestDto.durationMin(),
                eventSearchRequestDto.durationMax(),
                eventSearchRequestDto.locationId(),
                eventSearchRequestDto.eventStatus()
        );
    }
}