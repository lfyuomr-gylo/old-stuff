package com.github.lfyuomr.gylo.bostongene.task2.web;

import com.github.lfyuomr.gylo.bostongene.task2.data.User;
import com.github.lfyuomr.gylo.bostongene.task2.data.UserRepository;
import lombok.val;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Validated
public class UsersController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        Objects.requireNonNull(userRepository, "User repository should be non-null.");
        this.repository = userRepository;

        Objects.requireNonNull(passwordEncoder, "Password encoder should be non-null");
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public User getUser(@RequestParam("email") @Email String userEmail) throws UserNotFoundException {
        val user = repository.getUserByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException(userEmail);
        }
        return user;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User user) throws EmailAlreadyInUse {
        val encryptedPassword = passwordEncoder.encode(user.getPassword());
        user = user.withPassword(encryptedPassword);
        if (!repository.createUser(user)) {
            throw new EmailAlreadyInUse(user.getEmail());
        }
        return user;
    }

    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public void deleteUser(@RequestParam("email") @Email String email) throws UserNotFoundException {
        if (!repository.deleteUser(email)) {
            throw new UserNotFoundException(email);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, List<String>> handleUserNotFound(UserNotFoundException e) {
        return Collections.singletonMap("errors", Collections.singletonList(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, List<String>> handleEmailAlreadyInUse(EmailAlreadyInUse e) {
        return Collections.singletonMap("errors", Collections.singletonList(e.getMessage()));
    }

    private static class UserNotFoundException extends RuntimeException {
        UserNotFoundException(String email) {
            super("No user with email " + email + " found.");
        }
    }

    private static class EmailAlreadyInUse extends RuntimeException {
        EmailAlreadyInUse(String email) {
            super("User with email " + email + " already exists.");
        }
    }
}
