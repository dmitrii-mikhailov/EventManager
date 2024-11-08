package org.mikhailov.dm.eventmanager.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        String login,
        @NotBlank
        @Size(min = 3, max = 20)
        String password
) {
}
