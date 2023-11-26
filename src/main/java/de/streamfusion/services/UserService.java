package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.EditAccountDetailsRequest;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByID(long id) throws NoSuchElementException {
        return this.userRepository.findById(id).orElseThrow();
    }

    public User getUserByEmail(String email) throws NoSuchElementException {
        return this.userRepository.findByEmail(email).orElseThrow();
    }

    public void updateUser(User user, @NonNull EditAccountDetailsRequest request) throws IllegalArgumentException {
        if (request.newUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is empty.");
        }
        if (request.newEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is empty.");
        }
        if (this.emailIsNotValid(request.newEmail())) {
            throw new IllegalArgumentException("Email is not valid.");
        }
        if (request.newFirstname().isEmpty()) {
            throw new IllegalArgumentException("First name is empty.");
        }
        if (request.newLastname().isEmpty()) {
            throw new IllegalArgumentException("Last name is empty.");
        }
        user.setUsername(request.newUsername());
        user.setEmail(request.newEmail());
        user.setFirstName(request.newFirstname());
        user.setLastName(request.newLastname());
        user.setDateOfBirth(request.newDateOfBirth());
        this.userRepository.save(user);
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
}
