package org.mikhailov.dm.eventmanager.locations;

public record Location (
        Long id,
        String name,
        String address,
        Long capacity,
        String description
){}
