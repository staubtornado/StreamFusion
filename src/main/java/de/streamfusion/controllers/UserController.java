package de.streamfusion.controllers;

import de.streamfusion.controllers.requestAndResponse.RegisterRequest;
import de.streamfusion.controllers.requestAndResponse.UpdateRequest;
import de.streamfusion.exceptions.EmailAlreadyExistsException;
import de.streamfusion.exceptions.NoValidEmailException;
import de.streamfusion.exceptions.UsernameTakenException;
import de.streamfusion.models.User;
import de.streamfusion.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/user")
    public ModelAndView user(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("user");
        try {
            User user = this.userService.getUserByID(id).orElseThrow();
            modelAndView.addObject("user", user);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }

    @GetMapping("/account")
    public ModelAndView account(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("account");
        final User user;
        try {
            user = this.userService.getUserByID(id).orElseThrow();
            modelAndView.addObject("sendUrl", "/account?id=" + user.getID());
            modelAndView.addObject("user", user);
        } catch (NoSuchElementException e) {
            modelAndView.setViewName("redirect:/error");
        }
        return modelAndView;
    }

    @PostMapping("/account")
    public ResponseEntity<?> editUser(
            @RequestParam long id,
            @RequestBody String updateData
    ) {
        UpdateRequest request = userService.putIntoUpdateRequest(updateData);
        final User user;
        try {
            user = this.userService.getUserByID(id).orElseThrow();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            this.userService.updateUser(user, request);
        } catch (UsernameTakenException | NoValidEmailException | EmailAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Saved changes.", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody String registerData) {
        RegisterRequest request = userService.putIntoRegisterRequest(registerData);
        try {
            this.userService.addUser(request);
        } catch (EmailAlreadyExistsException | NoValidEmailException | UsernameTakenException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successfully created your account", HttpStatus.OK);
    }
}
