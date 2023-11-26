package de.streamfusion.controllers.requestAndResponse;

public record PasswordChangeRequest (
        String oldPassword,
        String newPassword
) {}
