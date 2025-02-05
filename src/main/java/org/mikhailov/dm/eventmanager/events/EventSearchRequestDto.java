package org.mikhailov.dm.eventmanager.events;

import java.time.LocalDateTime;

public record EventSearchRequestDto(
        String name,
        Long placesMin,
        Long placesMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        Long costMin,
        Long costMax,
        Long durationMin,
        Long durationMax,
        Long locationId,
        String eventStatus
) {
}
