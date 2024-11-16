package org.mikhailov.dm.eventmanager.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
                    18,
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
                    18,
                    UserRole.ADMIN.name()
            );
            userRepository.save(userEntity);
        }
    }
}