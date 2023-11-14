package de.streamfusion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {
    @GetMapping("/video")
    public String video(@RequestParam(name="id") String id) {
        return "Welcome to the video with the ID %s".formatted(id);
    }
}
