package de.streamfusion.services;

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

    public User getUserByEmail(@NonNull String email) throws NoSuchElementException {
        return this.userRepository.findByEmail(email).orElseThrow();
    }

}
