package com.secondhand.controller;

import com.secondhand.dto.user.UserChangePasswordRequest;
import com.secondhand.dto.user.UserDetailResponse;
import com.secondhand.dto.user.UserUpdateRequest;
import com.secondhand.service.JwtUtil;
import com.secondhand.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile operations.
 *
 * <p>Provides endpoints for the authenticated user to view and manage their own profile,
 * change their password, and delete their account.
 * Base path: {@code /api/user}</p>
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a {@code UserController} with the required service dependency.
     *
     * @param userService the user service used to handle profile management logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the full profile of the currently authenticated user.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @return the {@link UserDetailResponse} containing the authenticated user's profile data
     */
    @GetMapping
    public UserDetailResponse getProfile(@RequestHeader("Authorization") String token) {
        return userService.getProfile(JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Updates the profile information of the currently authenticated user.
     *
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing the updated profile fields
     * @return the updated {@link UserDetailResponse} reflecting the new profile data
     */
    @PutMapping("update-profile")
    public UserDetailResponse updateProfile(@RequestHeader("Authorization") String token, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(JwtUtil.getUserIdFromToken(token), request);
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing the current and new passwords
     */
    @PutMapping("change-password")
    public void changePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody UserChangePasswordRequest request) {
        userService.changePassword(JwtUtil.getUserIdFromToken(token), request);
    }

    /**
     * Permanently deletes the account of the currently authenticated user.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     */
    @DeleteMapping("delete-account")
    public void deleteProfile(@RequestHeader("Authorization") String token) {
        userService.deleteProfile(JwtUtil.getUserIdFromToken(token));
    }
}
