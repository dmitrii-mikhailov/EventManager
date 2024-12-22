package org.mikhailov.dm.eventmanager.events;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId,
        String status
) {
}