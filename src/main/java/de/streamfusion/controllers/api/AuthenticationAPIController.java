package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.*;
import de.streamfusion.services.AuthenticationService;
import io.jsonwebtoken.MalformedJwtException;
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

    @Autowired
    public AuthenticationAPIController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/edit-details")
    public ResponseEntity<AuthenticationResponse> editUserDetails(
            @NonNull @RequestBody EditAccountDetailsRequest request,
            @RequestHeader("Authorization") String token
    ) {
        AuthenticationResponse response;
        try {
            response = this.authenticationService.editUser(request, token);
        } catch (NoSuchElementException e) {
            response = new AuthenticationResponse("User not found.", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (MalformedJwtException e) {
            response = new AuthenticationResponse("Invalid token.", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response = new AuthenticationResponse(e.getMessage(), token.replace("Bearer ", ""));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(
            @NonNull @RequestBody DeleteAccountRequest request,
            @RequestHeader("Authorization") String token
    ) {
        try {
            this.authenticationService.deleteUser(request, token);
        } catch (BadCredentialsException | MalformedJwtException e) {
            return new ResponseEntity<>("Authorization failed.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Successfully deleted user.", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @NonNull @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String token
    ) {
        final AuthenticationResponse response;
        try {
            response = this.authenticationService.changePassword(request, token);
        } catch (BadCredentialsException | MalformedJwtException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        AuthenticationResponse response;
        try {
            response = this.authenticationService.register(request);
        } catch (IllegalArgumentException e) {
            response = new AuthenticationResponse(e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
