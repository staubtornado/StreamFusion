package de.streamfusion.controllers.requestAndResponse;

public record ChangePasswordRequest (
        String oldPassword,
        String newPassword
) {}
