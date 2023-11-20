package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.services.UserService;
import de.streamfusion.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
public class PageController {
    private final VideoService videoService;
    private final UserService userService;

    @Autowired
    public PageController(VideoService videoService, UserService userService) {
        this.videoService = videoService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/account")
    public String account(Model model) {
        return "account";
    }

    @GetMapping("/video")
    public ModelAndView video(@RequestParam(name="id") long id, ModelAndView modelAndView) {
        final Video video;
        try {
            video = this.videoService.getVideoByID(id).orElseThrow();
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("error");
            return modelAndView;
        }

        modelAndView.addObject("id", id);
        modelAndView.addObject("title", video.getTitle());
        modelAndView.addObject("likes", video.getLikes());
        modelAndView.addObject("dislikes", video.getDislikes());
        modelAndView.addObject("views", video.getViews());
        modelAndView.addObject("description", video.getDescription());
        modelAndView.addObject("filetype", video.getFiletype());
        modelAndView.addObject("streamURL", "/cdn/v?id=%d".formatted(id));
        modelAndView.addObject("thumbnailURL", "/cdn/thumbnail?id=%d".formatted(id));
        return modelAndView;
    }

    @GetMapping("/upload")
    public String upload(Model model) {
        return "upload";
    }

    @GetMapping("/user")
    public ModelAndView user(@RequestParam(name="id") long id, ModelAndView model){
        final User user;
        try {
            user = this.userService.getUserByID(id).orElseThrow();
        } catch (NoSuchElementException e) {
            model.setViewName("error");
            return model;
        }

        model.addObject("id", id);
        model.addObject("username", user.getUsername());
        model.addObject("email", user.getEmail());
        model.addObject("videos", user.getVideos());
        model.addObject("password", user.getPassword());
        model.addObject("firstname", user.getFirstName());
        model.addObject("lastname", user.getLastName());
        model.addObject("dateOfBirth", user.getDateOfBirth());
        model.addObject("picture", "/cdn/u/picture/?id=%d".formatted(id));
        return model;
    }

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }
}
