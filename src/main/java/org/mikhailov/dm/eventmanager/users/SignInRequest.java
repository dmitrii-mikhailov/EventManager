package org.mikhailov.dm.eventmanager.users;

public record SignInRequest(
        String login,
        String password)
{}
