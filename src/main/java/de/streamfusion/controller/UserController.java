package de.streamfusion.controller;

import de.streamfusion.Exceptions.EmailAlreadyExistsException;
import de.streamfusion.Exceptions.NoValidEmailException;
import de.streamfusion.Exceptions.UsernameTakenException;
import de.streamfusion.models.User;
import de.streamfusion.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void registerUser(@RequestBody User user) {
        try {
            this.userService.addUser(user);
        } catch (EmailAlreadyExistsException e) { //TODO: HTTP-Response Types!

        } catch (NoValidEmailException e) {

        } catch (UsernameTakenException e) {

        }
    }

}
