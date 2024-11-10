package org.mikhailov.dm.eventmanager.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public CreateUserService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignUpRequest signUpRequest) {
        if (userService.userExists(signUpRequest.login())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(
                null,
                signUpRequest.login(),
                passwordEncoder.encode(signUpRequest.password()),
                signUpRequest.age(),
                UserRole.USER
        );

        return userService.createUser(user);
    }
}
