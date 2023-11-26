package de.streamfusion.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    public PageController() {
    }

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
    public String login() {
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
