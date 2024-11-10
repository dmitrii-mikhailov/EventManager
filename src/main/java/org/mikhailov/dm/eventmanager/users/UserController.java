package org.mikhailov.dm.eventmanager.users;

import jakarta.validation.Valid;
import org.mikhailov.dm.eventmanager.security.jwt.JwtAuthenticationService;
import org.mikhailov.dm.eventmanager.security.jwt.JwtTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final CreateUserService createUserService;

    public UserController(UserService userService, UserDtoConverter userDtoConverter, JwtAuthenticationService jwtAuthenticationService, CreateUserService createUserService) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.createUserService = createUserService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("POST request for create user {}", signUpRequest.login());
        User newUser = createUserService.createUser(signUpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDtoConverter.toDto(newUser));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        log.info("GET request for user id {}", userId);
        User user = userService.getUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtoConverter.toDto(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authUser(@RequestBody @Valid SignInRequest signInRequest) {
        log.info("POST request for sign in user {}", signInRequest.login());
        String token = jwtAuthenticationService.authenticateUser(signInRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }
}