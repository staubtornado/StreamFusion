package de.streamfusion.controllers;

import de.streamfusion.models.Video;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ModelAndView video(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("video");
        try {
            Video video = this.videoService.getVideoByID(id).orElseThrow();
            modelAndView.addObject("video", video);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }
}
