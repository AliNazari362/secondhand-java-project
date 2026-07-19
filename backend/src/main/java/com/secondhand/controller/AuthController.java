package com.secondhand.controller;

import com.secondhand.dto.user.UserDetailResponse;
import com.secondhand.dto.user.UserLoginRequest;
import com.secondhand.dto.user.UserRegisterRequest;
import com.secondhand.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <p>Handles user registration and login requests.
 * Base path: {@code /api/auth}</p>
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs an {@code AuthController} with the required service dependency.
     *
     * @param authService the authentication service used to handle registration and login logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account with the provided details.
     *
     * @param registerRequest the validated request body containing the new user's registration data
     * @return the {@link UserDetailResponse} representing the newly created user
     */
    @PostMapping("register")
    public UserDetailResponse register(@Valid @RequestBody UserRegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     *
     * @param loginRequest the validated request body containing the user's credentials
     * @return a JWT token string to be used in subsequent authenticated requests
     */
    @PostMapping("login")
    public String login(@Valid @RequestBody UserLoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
