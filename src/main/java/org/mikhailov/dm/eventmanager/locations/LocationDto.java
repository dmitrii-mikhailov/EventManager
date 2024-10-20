package org.mikhailov.dm.eventmanager.locations;

import jakarta.validation.constraints.*;

public record LocationDto (
        @Null
        Long id,
        @Size(max = 1024)
        @NotBlank
        String name,
        @Size(max = 10000)
        @NotBlank
        String address,
        @NotNull
        @Min(5)
        Long capacity,
        @Size(max = 100000)
        @NotBlank
        String description
){}
