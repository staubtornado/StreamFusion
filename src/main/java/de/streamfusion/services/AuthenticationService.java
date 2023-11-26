package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.AuthenticationRequest;
import de.streamfusion.controllers.requestAndResponse.AuthenticationResponse;
import de.streamfusion.controllers.requestAndResponse.RegisterRequest;
import de.streamfusion.models.Role;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
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
        String token = this.jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest authenticationRequest) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );
        final User user = this.userRepository.findByEmail(authenticationRequest.email()).orElseThrow();
        String token = this.jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public String getEmailFromToken(String token) {
        return this.jwtService.getUsernameFromToken(token);
    }

    public String generateToken(@NonNull User user) {
        return this.jwtService.generateToken(user);
    }

}
