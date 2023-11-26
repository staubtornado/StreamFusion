package de.streamfusion.controllers.requestAndResponse;

public record EditAccountDetailsRequest(
        String newUsername,
        String newEmail,
        String newFirstname,
        String newLastname,
        Long newDateOfBirth
) {}
