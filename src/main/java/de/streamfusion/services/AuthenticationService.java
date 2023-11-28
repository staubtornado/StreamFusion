package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.*;
import de.streamfusion.models.Role;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(@NonNull RegisterRequest registerRequest) {
        if (this.userRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (this.userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (this.usernameIsNotValid(registerRequest.username())) {
            throw new IllegalArgumentException("Username is not valid.");
        }
        if (this.emailIsNotValid(registerRequest.email())) {
            throw new IllegalArgumentException("Email is not valid.");
        }
        final User user = new User(
                registerRequest.username(),
                registerRequest.email(),
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.dateOfBirth(),
                this.passwordEncoder.encode(registerRequest.password()),
                Role.USER
        );
        this.userRepository.save(user);
        String token = this.generateToken(user);
        return new AuthenticationResponse("Successfully registered.", token);
    }

    public AuthenticationResponse changePassword(@NonNull ChangePasswordRequest request, @NonNull String token) {
        token = this.extractToken(token);
        final String email = this.getEmailFromToken(token);
        matchCredentials(email, request.oldPassword());

        final User user = this.userRepository.findByEmail(email).orElseThrow();
        user.setPassword(this.passwordEncoder.encode(request.newPassword()));
        this.userRepository.save(user);
        token = this.generateToken(user);
        return new AuthenticationResponse("Successfully changed password.", token);
    }

    public void deleteUser(@NonNull DeleteAccountRequest request, @NonNull String token) {
        token = this.extractToken(token);
        String email = this.getEmailFromToken(token);

        this.matchCredentials(email, request.password());
        final User user = this.userRepository.findByEmail(email).orElseThrow();
        this.userRepository.delete(user);
    }

    public AuthenticationResponse editUser(@NonNull EditAccountDetailsRequest request, @NonNull String token) {
        token = this.extractToken(token);
        String email = this.getEmailFromToken(token);

        if (this.emailIsNotValid(request.newEmail())) {
            throw new IllegalArgumentException("Email is not valid.");
        }

        final User user = this.userRepository.findByEmail(email).orElseThrow();
        if (this.userRepository.existsByEmailAndIdNot(request.newEmail(), user.getID())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (this.userRepository.existsByUsernameAndIdNot(request.newUsername(), user.getID())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        user.setUsername(request.newUsername());
        user.setEmail(request.newEmail());
        user.setFirstName(request.newFirstname());
        user.setLastName(request.newLastname());
        user.setDateOfBirth(request.newDateOfBirth());
        this.userRepository.save(user);

        if (!request.newEmail().equals(email)) {
            token = this.generateToken(user);
        }
        return new AuthenticationResponse("Successfully edited user.", token);
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest authenticationRequest) {
        this.matchCredentials(authenticationRequest.email(), authenticationRequest.password());
        final User user = this.userRepository.findByEmail(authenticationRequest.email()).orElseThrow();
        String token = this.generateToken(user);
        return new AuthenticationResponse("Successfully authenticated.", token);
    }

    public String getEmailFromToken(String token) {
        return this.jwtService.getUsernameFromToken(token);
    }

    public String generateToken(@NonNull User user) {
        return this.jwtService.generateToken(user);
    }

    public void matchCredentials(String email, String password) throws BadCredentialsException {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }

    private @NonNull String extractToken(@NonNull String token) {
        if (token.matches("Bearer .*")) {
            token = token.substring(7);
        }
        return token;
    }

    /**
     * Checks a given String if it contains all the characteristics of an email.
     *
     * @param email a String representing the users email
     * @return true if email is <i>not</i> valid
     */
    private boolean emailIsNotValid(@NonNull String email) {
        return !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean usernameIsNotValid(@NonNull String username) {
        return !username.matches("^[a-z\\d]{1,10}$");
    }
}
