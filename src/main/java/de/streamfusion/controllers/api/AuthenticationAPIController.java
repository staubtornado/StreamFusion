package de.streamfusion.controllers.api;

import de.streamfusion.controllers.requestAndResponse.*;
import de.streamfusion.services.AuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationAPIController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationAPIController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PutMapping("/edit-details")
    public ResponseEntity<String> editUserDetails(
            @NonNull @RequestBody EditAccountDetailsRequest request,
            @RequestHeader("Cookie") String cookies,
            HttpServletResponse response
    ) {
        final String newToken;
        try {
            newToken = this.authenticationService.editUser(request, cookies);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        } catch (MalformedJwtException | ExpiredJwtException e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (newToken != null) {
            response.addCookie(AuthenticationService.generateCookie(newToken));
            return new ResponseEntity<>("Successfully changed account details.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Successfully changed account details.", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(
            @NonNull @RequestBody DeleteAccountRequest request,
            @RequestHeader("Cookie") String cookies
    ) {
        try {
            this.authenticationService.deleteUser(request, cookies);
        } catch (BadCredentialsException | MalformedJwtException | ExpiredJwtException e) {
            return new ResponseEntity<>("Authorization failed.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Successfully deleted user.", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @NonNull @RequestBody ChangePasswordRequest request,
            @RequestHeader("Cookie") String cookies,
            HttpServletResponse response
    ) {
        final String token;
        try {
            token = this.authenticationService.changePassword(request, cookies);
        } catch (BadCredentialsException | MalformedJwtException | ExpiredJwtException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        response.addCookie(AuthenticationService.generateCookie(token));
        return new ResponseEntity<>("Successfully changed password.", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request, HttpServletResponse response) {
        final String token;
        try {
            token = this.authenticationService.register(request);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.addCookie(AuthenticationService.generateCookie(token));
        return new ResponseEntity<>("Successfully registered.", HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(
            @RequestBody AuthenticationRequest loginData,
            HttpServletResponse response
    ) {
        final String token;
        try {
            token = this.authenticationService.authenticate(loginData);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        response.addCookie(AuthenticationService.generateCookie(token));
        return new ResponseEntity<>("Successfully authenticated.", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@NonNull HttpServletResponse response) {
        response.addCookie(AuthenticationService.generateCookie(""));
        return new ResponseEntity<>("Successfully logged out.", HttpStatus.OK);
    }
}
