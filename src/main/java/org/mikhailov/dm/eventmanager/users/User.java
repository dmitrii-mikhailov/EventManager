package org.mikhailov.dm.eventmanager.users;

public record User (
        Long id,
        String login,
        String passwordHash,
        Integer age,
        UserRole role
){}
