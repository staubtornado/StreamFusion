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
    public ModelAndView account() {
        ModelAndView modelAndView = new ModelAndView("account");
        modelAndView.addObject("user", null);
        return modelAndView;
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
