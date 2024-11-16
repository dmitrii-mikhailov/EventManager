package org.mikhailov.dm.eventmanager.users;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEntityConverter userEntityConverter;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserEntityConverter userEntityConverter, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userEntityConverter = userEntityConverter;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
       UserEntity userEntity = new UserEntity(
                null,
                user.login(),
                passwordEncoder.encode(user.passwordHash()),
                user.age(),
                UserRole.USER.name()
        );

        UserEntity savedEntity =
                userRepository.save(userEntity);

        return userEntityConverter.toDomain(savedEntity);
    }

    public User getUser(Long userId) {
        return userEntityConverter.toDomain(userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("No user found with id: " + userId)));
    }

    public User findByLogin(String login) {
        return userEntityConverter.toDomain(userRepository.findByLogin(login)
                .orElseThrow(()->new EntityNotFoundException("User not found")));
    }

    public boolean userExists(String login) {
        return userRepository.existsByLogin(login);
    }
}