package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.services.AuthenticationService;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;
    private final AuthenticationService authenticationService;

    @Autowired
    public VideoController(VideoService videoService, AuthenticationService authenticationService) {
        this.videoService = videoService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public ModelAndView video(
            @RequestParam long id,
            @RequestHeader(name = "Cookie", required = false) String cookies
    ) {
        ModelAndView modelAndView = new ModelAndView("video");
        final Video video;
        final User uploader;
        try {
            video = this.videoService.getVideoByID(id);
            uploader = video.getUser();
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
            return modelAndView;
        }
        User user = null;
        try {
            user = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
        } catch (IllegalArgumentException ignored) {}

        modelAndView.addObject("video", video);
        modelAndView.addObject("uploader", uploader);
        modelAndView.addObject("user", user);
        this.videoService.addView(id);
        return modelAndView;
    }

    @GetMapping("/new")
    public String upload() {
        return "upload";
    }

    @GetMapping("/edit")
    public ModelAndView editVideo(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("edit");
        try {
            Video video = this.videoService.getVideoByID(id);
            modelAndView.addObject("video", video);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }
}
