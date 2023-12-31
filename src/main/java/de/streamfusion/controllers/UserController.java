package de.streamfusion.controllers;

import de.streamfusion.models.User;
import de.streamfusion.services.AuthenticationService;
import de.streamfusion.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping(value = "/user")
    public ModelAndView user(@RequestParam long id, @RequestHeader(value = "Cookie", required = false) String cookies) {
        ModelAndView modelAndView = new ModelAndView("user");
        try {
            final User account = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
            modelAndView.addObject("account", account);
        } catch (IllegalArgumentException ignored) {}

        try {
            User user = this.userService.getUserByID(id);
            modelAndView.addObject("user", user);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }

    @GetMapping(value = "/account")
    public ModelAndView account(@RequestHeader("Cookie") String cookies) {
        ModelAndView modelAndView = new ModelAndView("account");
        try {
            final User account = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
            modelAndView.addObject("account", account);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }
}
