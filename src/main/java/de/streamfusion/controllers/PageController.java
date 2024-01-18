package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.services.AuthenticationService;
import de.streamfusion.services.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {
    private final AuthenticationService authenticationService;
    private final VideoService videoController;

    @Autowired
    public PageController(AuthenticationService authenticationService, VideoService videoController) {
        this.authenticationService = authenticationService;
        this.videoController = videoController;
    }

    @GetMapping("/")
    public ModelAndView home(@RequestHeader(name = "Cookie", required = false) String cookies) {
        ModelAndView modelAndView = new ModelAndView("home");
        try {
            final User account = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
            modelAndView.addObject("account", account);
        } catch (IllegalArgumentException ignored) {}
        modelAndView.addObject("videos", this.videoController.getRecommendedVideos());
        return modelAndView;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "logout", required = false) String logout, HttpServletResponse response) {
        if (logout != null) {
            response.addCookie(AuthenticationService.generateCookie(null));
        }
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
