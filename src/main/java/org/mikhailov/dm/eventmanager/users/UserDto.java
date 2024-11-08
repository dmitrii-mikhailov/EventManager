package org.mikhailov.dm.eventmanager.users;

import jakarta.validation.constraints.*;

public record UserDto (
        @Null
        Long id,
        @Size(max = 1024)
        @NotBlank
        String login
){}
