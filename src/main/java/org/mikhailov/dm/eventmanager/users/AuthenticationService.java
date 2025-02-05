package org.mikhailov.dm.eventmanager.users;

import org.mikhailov.dm.eventmanager.security.jwt.JwtTokenManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JwtTokenManager jwtTokenManager, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(SignInRequest signInRequest) {
        if (!userService.userExists(signInRequest.login())) {
            throw new BadCredentialsException("Bad credentials login");
        }
        User user = userService.findByLogin(signInRequest.login());
        if (!passwordEncoder.matches(signInRequest.password(), user.passwordHash())) {
            throw new BadCredentialsException("Bad credentials password");
        }
        return jwtTokenManager.generateToken(user.login());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        }
        return (User) authentication.getPrincipal();
    }
}
