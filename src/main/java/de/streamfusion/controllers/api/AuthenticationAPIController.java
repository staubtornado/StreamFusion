package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.*;
import de.streamfusion.models.User;
import de.streamfusion.services.AuthenticationService;
import de.streamfusion.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationAPIController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public AuthenticationAPIController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/edit-details")
    public ResponseEntity<EditAccountDetailsResponse> editUserDetails(
            @NonNull @RequestBody EditAccountDetailsRequest request,
            @RequestHeader("Authorization") String token
    ) {
        token = token.substring(7);
        final String email = this.authenticationService.getEmailFromToken(token);
        final User user;

        try {
            user = this.userService.getUserByEmail(email);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final boolean subjectChanged = !request.newEmail().equals(email);
        final EditAccountDetailsResponse response;
        try {
            this.userService.updateUser(user, request);
        } catch (IllegalArgumentException e) {
            response = new EditAccountDetailsResponse(e.getMessage(), token);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (subjectChanged) {
            token = this.authenticationService.generateToken(user);
        }
        response = new EditAccountDetailsResponse("Successfully updated user.", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        final AuthenticationResponse response;
        try {
            response = this.authenticationService.register(request);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest loginData) {

        final AuthenticationResponse response;
        try {
            response = this.authenticationService.authenticate(loginData);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
