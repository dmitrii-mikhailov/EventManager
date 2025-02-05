package org.mikhailov.dm.eventmanager.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank
        String name,
        @Min(1)
        Integer maxPlaces,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSzz")
        @Future
        LocalDateTime date,
        @Min(0)
        Integer cost,
        @Min(30)
        Integer duration,
        @Min(1)
        Long locationId
) {
}
