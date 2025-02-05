package org.mikhailov.dm.eventmanager.events.registrations;

import org.mikhailov.dm.eventmanager.events.EventEntity;

public record Registration(
        Long id,
        Long userId,
        Long eventId
) {
}
