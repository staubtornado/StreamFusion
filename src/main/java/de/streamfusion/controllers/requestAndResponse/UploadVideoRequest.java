package de.streamfusion.controllers.requestAndResponse;

import org.springframework.web.multipart.MultipartFile;

public record UploadVideoRequest(
        MultipartFile file,
        String title,
        String description
) {}