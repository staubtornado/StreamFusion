package de.streamfusion.controllers.requestAndResponse;

public record EditAccountDetailsRequest(
        String newUsername,
        String newEmail,
        String newFirstName,
        String newLastName,
        String newDateOfBirth,
        String newProfilePicture,
        String newBannerPicture
) {}
