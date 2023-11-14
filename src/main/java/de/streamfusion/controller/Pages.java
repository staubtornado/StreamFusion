package de.streamfusion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Pages {
    @GetMapping("/")
    public String home() {
        return "Welcome to StreamFusion!";
    }

    @GetMapping("/register")
    public String register() {
        return "Welcome to your register!";
    }

    @GetMapping("/login")
    public String login() {
        return "Welcome to your login!";
    }

    @GetMapping("/account")
    public String account() {
        return "Welcome to your account!";
    }
}
