package de.streamfusion.controllers;

import de.streamfusion.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {
    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", null);
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
