package org.mikhailov.dm.eventmanager.listeners;

import org.mikhailov.dm.eventmanager.users.UserEntity;
import org.mikhailov.dm.eventmanager.users.UserRepository;
import org.mikhailov.dm.eventmanager.users.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Listeners {

    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Listeners(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextStartedEvent.class)
    public void createDefaultUsers() {
        if (!userRepository.existsByLogin("user")) {
            log.info("Creating 'user'");
            UserEntity userEntity = new UserEntity(
                    null,
                    "user",
                    passwordEncoder.encode("user"),
                    UserRole.USER.name()
            );
            userRepository.save(userEntity);
        }

        if (!userRepository.existsByLogin("admin")) {
            log.info("Creating 'admin'");
            UserEntity userEntity = new UserEntity(
                    null,
                    "admin",
                    passwordEncoder.encode("admin"),
                    UserRole.ADMIN.name()
            );
            userRepository.save(userEntity);
        }
    }
}