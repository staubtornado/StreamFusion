package de.streamfusion.services;

import de.streamfusion.Exceptions.EmailAlreadyExistsException;
import de.streamfusion.Exceptions.NoValidEmailException;
import de.streamfusion.Exceptions.UsernameTakenException;
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
            throw new EmailAlreadyExistsException("Email " + user.getEmail() + " already exists");
        }
        if (!((user.getEmail().contains("@") && (user.getEmail().contains("."))))) {
            throw new NoValidEmailException("Your email must contain '@' and '.' to be valid");
        }
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameTakenException(user.getUsername() + " is already taken");
        }
        this.userRepository.save(user);
    }
}
