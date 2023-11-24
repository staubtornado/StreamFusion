package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.UpdateRequest;
import de.streamfusion.exceptions.EmailAlreadyExistsException;
import de.streamfusion.exceptions.NoValidEmailException;
import de.streamfusion.exceptions.UsernameTakenException;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByID(long id) {
        return this.userRepository.findById(id);
    }

    public void addUser(User user) throws EmailAlreadyExistsException, NoValidEmailException, UsernameTakenException {
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email %s already exists".formatted(user.getEmail()));
        }
        if (emailIsNotValid(user.getEmail())){
            throw new NoValidEmailException("Email %s is not valid".formatted(user.getEmail()));
        }
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameTakenException("Username %s is already taken".formatted(user.getUsername()));
        }
        this.userRepository.save(user);
    }

    public void updateUser(User user, UpdateRequest request
    ) throws UsernameTakenException, NoValidEmailException, EmailAlreadyExistsException {

        if (this.userRepository.existsByUsername(request.getNewUsername())) {
            throw new UsernameTakenException("Username %s is already taken".formatted(user.getUsername()));
        }
        if (emailIsNotValid(request.getNewEmail())) {
            throw new NoValidEmailException("Email %s is not valid".formatted(user.getEmail()));
        }
        if (this.userRepository.existsByEmail(request.getNewEmail())) {
            throw new EmailAlreadyExistsException("Email %s already exists".formatted(user.getEmail()));
        }

        user.setUsername(request.getNewUsername());
        user.setEmail(request.getNewEmail());
        user.setFirstName(request.getNewFirstname());
        user.setLastName(request.getNewLastname());
        user.setDateOfBirth(request.getNewDate());
        user.setPassword(request.getNewPassword());
        this.userRepository.save(user);
    }

    /**
     * Checks a given String if it contains all the characteristics of an email.
     *
     * @param email a String representing the users email
     * @return true if email is <i>not</i> valid
     */
    private boolean emailIsNotValid(String email) {
        return !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
