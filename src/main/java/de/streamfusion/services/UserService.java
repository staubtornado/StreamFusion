package de.streamfusion.services;

import de.streamfusion.controllers.requestAndResponse.RegisterRequest;
import de.streamfusion.controllers.requestAndResponse.UpdateRequest;
import de.streamfusion.exceptions.EmailAlreadyExistsException;
import de.streamfusion.exceptions.NoValidEmailException;
import de.streamfusion.exceptions.UsernameTakenException;
import de.streamfusion.models.Role;
import de.streamfusion.models.User;
import de.streamfusion.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository
//            , PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByID(long id) {
        return this.userRepository.findById(id);
    }

    public void addUser(RegisterRequest request) throws EmailAlreadyExistsException, NoValidEmailException, UsernameTakenException {
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getFirstname(),
                request.getLastname(),
                request.getDateOfBirth(),
//                this.passwordEncoder.encode(request.getPassword()),
                request.getPassword(),
                Role.USER
        );
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

    public RegisterRequest putIntoRegisterRequest(String registerData) {
        String[] contentArray = registerData.split("&");
        return new RegisterRequest(
                contentArray[0].split("=")[1],
                contentArray[1].split("=")[1].replace("%40", "@"),
                contentArray[2].split("=")[1],
                contentArray[3].split("=")[1],
                Long.parseLong(contentArray[4].split("=")[1]),
                contentArray[5].split("=")[1]
        );
    }

    //TODO: Make Null safe!
    public UpdateRequest putIntoUpdateRequest(String updateData) {
        String[] contentArray = updateData.split("&");
        UpdateRequest updateRequest = new UpdateRequest(
                contentArray[1].split("=")[1],
                contentArray[2].split("=")[1].replace("%40", "@"),
                contentArray[3].split("=")[1],
                contentArray[4].split("=")[1],
                Long.parseLong(contentArray[5].split("=")[1]),
                contentArray[6].split("=")[1]
        );

        return updateRequest;
    }

}
