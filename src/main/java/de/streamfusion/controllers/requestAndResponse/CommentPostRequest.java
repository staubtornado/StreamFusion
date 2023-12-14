package de.streamfusion.controllers.requestAndResponse;

public record CommentPostRequest(
        long videoID,
        String commentContent
) {}
