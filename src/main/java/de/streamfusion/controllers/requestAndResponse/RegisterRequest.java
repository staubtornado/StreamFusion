package de.streamfusion.controllers.requestAndResponse;

public record RegisterRequest (
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        long dateOfBirth
) {}