package de.streamfusion.services;

import de.streamfusion.exceptions.EmailAlreadyExistsException;
import de.streamfusion.exceptions.NoValidEmailException;
import de.streamfusion.exceptions.UsernameTakenException;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) throws EmailAlreadyExistsException, NoValidEmailException, UsernameTakenException {
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email %s already exists".formatted(user.getEmail()));
        }
        if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new NoValidEmailException("Email %s is not valid".formatted(user.getEmail()));
        }
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameTakenException("Username %s is already taken".formatted(user.getUsername()));
        }
        this.userRepository.save(user);
    }
}
