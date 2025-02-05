package org.mikhailov.dm.eventmanager.events;

import org.mikhailov.dm.eventmanager.events.registrations.Registration;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        List<Registration> registrationList,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId,
        String status
) {
}
