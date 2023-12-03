package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.*;
import de.streamfusion.models.Role;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;

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

    /**
        * Registers the user.
        *
        * @param registerRequest The register request.
        * @return The generated token.
     */
    public String register(@NonNull RegisterRequest registerRequest) {
        if (this.userRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (this.userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (usernameIsNotValid(registerRequest.username())) {
            throw new IllegalArgumentException("Username is not valid. Only lowercase letters and numbers are allowed.");
        }
        if (emailIsNotValid(registerRequest.email())) {
            throw new IllegalArgumentException("Email is not valid.");
        }
        if (dateOfBirthIsNotValid(registerRequest.dateOfBirth())) {
            throw new IllegalArgumentException("Date of birth is not valid. Format: YYYY-MM-DD");
        }

        // Convert the date of birth to a long value representing the milliseconds since January 1, 1970, 00:00:00 GMT
        long dateOfBirth = Date.valueOf(registerRequest.dateOfBirth()).getTime();
        final User user = new User(
                registerRequest.username(),
                registerRequest.email(),
                registerRequest.firstName(),
                registerRequest.lastName(),
                dateOfBirth,
                this.passwordEncoder.encode(registerRequest.password()),
                Role.USER
        );
        this.userRepository.save(user);
        return this.generateToken(user);
    }

    /**
        * Changes the password of the user.
        *
        * @param request The change password request.
        * @param cookies The cookies of the user.
        * @return The new token.
     */
    public String changePassword(@NonNull ChangePasswordRequest request, @NonNull String cookies) {
        final String token = extractTokenFromCookie(cookies);
        final String email = this.getEmailFromToken(token);
        matchCredentials(email, request.oldPassword());

        final User user = this.userRepository.findByEmail(email).orElseThrow();
        user.setPassword(this.passwordEncoder.encode(request.newPassword()));
        this.userRepository.save(user);
        return this.generateToken(user);
    }

    /**
        * Deletes the user.
        *
        * @param request The delete account request.
        * @param cookies The cookies of the user.
     */
    public void deleteUser(@NonNull DeleteAccountRequest request, @NonNull String cookies) {
        String token = extractTokenFromCookie(cookies);
        String email = this.getEmailFromToken(token);

        this.matchCredentials(email, request.password());
        final User user = this.userRepository.findByEmail(email).orElseThrow();
        this.userRepository.delete(user);
    }

    /**
        * Edits the user.
        *
        * @param request The edit account details request.
        * @param cookies The cookies of the user.
        * @return Null if the email was not changed, the new token otherwise.
     */
    public String editUser(@NonNull EditAccountDetailsRequest request, @NonNull String cookies) {
        final String token = extractTokenFromCookie(cookies);
        final String email = this.getEmailFromToken(token);

        if (emailIsNotValid(request.newEmail())) {
            throw new IllegalArgumentException("Email is not valid.");
        }

        final User user = this.userRepository.findByEmail(email).orElseThrow();
        if (this.userRepository.existsByEmailAndIdNot(request.newEmail(), user.getID())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (this.userRepository.existsByUsernameAndIdNot(request.newUsername(), user.getID())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (usernameIsNotValid(request.newUsername())) {
            throw new IllegalArgumentException("Username should only contain lowercase letters and numbers.");
        }
        if (emailIsNotValid(request.newEmail())) {
            throw new IllegalArgumentException("Email is not valid.");
        }

        user.setUsername(request.newUsername());
        user.setEmail(request.newEmail());
        user.setFirstName(request.newFirstname());
        user.setLastName(request.newLastname());
        user.setDateOfBirth(request.newDateOfBirth());
        this.userRepository.save(user);

        if (!request.newEmail().equals(email)) {
            return this.generateToken(user);
        }
        return null;
    }

    /**
        * Authenticates the user.
        *
        * @param authenticationRequest The authentication request.
        * @return The generated token.
     */
    public String authenticate(@NonNull AuthenticationRequest authenticationRequest) {
        this.matchCredentials(authenticationRequest.email(), authenticationRequest.password());
        final User user = this.userRepository.findByEmail(authenticationRequest.email()).orElseThrow();
        return this.generateToken(user);
    }

    /**
        * Extracts the email from the token.
        *
        * @param token The token to extract the email from.
        * @return The extracted email.
     */
    public String getEmailFromToken(String token) {
        return this.jwtService.getUsernameFromToken(token);
    }

    /**
        * Generates a token for the given user.
        *
        * @param user The user to generate the token for.
        * @return The generated token.
     */
    public String generateToken(@NonNull User user) {
        return this.jwtService.generateToken(user);
    }

    /**
        * Matches the credentials against the database.
        *
        * @param email The email to match.
        * @param password The password to match.
        * @throws BadCredentialsException If the credentials are not valid.
     */
    public void matchCredentials(String email, String password) throws BadCredentialsException {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }

    /**
        * Email must contain an @ symbol.
        * Email must contain a domain name.
        *
        * @param email The email to check.
        * @return True if the email is not valid, false otherwise.
     */
    private static boolean emailIsNotValid(@NonNull String email) {
        return !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /**
        * Username must be between 1 and 10 characters long.
        * Username must only contain lowercase letters and numbers.
        *
        * @param username The username to check.
        * @return True if the username is not valid, false otherwise.
     */
    private static boolean usernameIsNotValid(@NonNull String username) {
        return !username.matches("^[a-z\\d]{1,20}$");
    }

    /**
     * Extracts the token from the cookie.
     * @param cookie The cookie to extract the token from.
     * @return The extracted token. If no token was found, an empty string is returned.
     */
    public static @NonNull String extractTokenFromCookie(@NonNull String cookie) {
        String[] cookieParts = cookie.split("; ");
        for (String cookiePart : cookieParts) {
            if (cookiePart.startsWith("Authorization=")) {
                return cookiePart.substring(14);
            }
        }
        return "";
    }

    public static @NonNull Cookie generateCookie(String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public @NonNull User getUserFromToken(String token) {
        final String email = this.getEmailFromToken(token);
        return this.userRepository.findByEmail(email).orElseThrow();
    }

    private static boolean dateOfBirthIsNotValid(@NonNull String dateOfBirth) {
        return !dateOfBirth.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
    }
}
