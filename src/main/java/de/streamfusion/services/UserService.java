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
