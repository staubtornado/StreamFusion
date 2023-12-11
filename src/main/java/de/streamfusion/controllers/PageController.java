package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
public class PageController {
    private final AuthenticationService authenticationService;

    @Autowired
    public PageController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public ModelAndView home(@RequestHeader(name = "Cookie", required = false) String cookies) {
        User user = null;
        try {
            user = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
        } catch (IllegalArgumentException ignored) {}

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("user", null);
        return modelAndView;
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
