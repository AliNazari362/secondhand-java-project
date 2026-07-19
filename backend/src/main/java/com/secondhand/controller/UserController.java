package com.secondhand.controller;

import com.secondhand.dto.user.UserChangePasswordRequest;
import com.secondhand.dto.user.UserDetailResponse;
import com.secondhand.dto.user.UserUpdateRequest;
import com.secondhand.exception.IllegalTokenException;
import com.secondhand.service.JwtUtil;
import com.secondhand.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user profile operations.
 *
 * <p>Provides endpoints for the authenticated user to view and manage their own profile,
 * change their password, and delete their account.
 * All endpoints require a valid JWT token passed in the {@code Authorization} header
 * with the {@code Bearer} scheme.</p>
 *
 * <p>Base path: {@code /api/user}</p>
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
     * Extracts the raw JWT token from the {@code Authorization} header value.
     * The header must start with the {@code "Bearer "} prefix; otherwise, an exception is thrown.
     *
     * @param header the full value of the {@code Authorization} header
     * @return the raw JWT token without the prefix
     * @throws IllegalTokenException if the header is {@code null}, empty, or does not start with {@code "Bearer "}
     */
    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalTokenException("توکن نامعتبر است");
        }
        return header.substring(7); // remove "Bearer " prefix
    }

    /**
     * Retrieves the full profile of the currently authenticated user.
     *
     * <p>The user ID is extracted from the provided JWT token.</p>
     *
     * @param authorization the JWT bearer token from the {@code Authorization} request header
     * @return the {@link UserDetailResponse} containing the authenticated user's profile data
     * @throws IllegalTokenException if the token is missing, malformed, expired, or invalid
     * @throws com.secondhand.exception.ResourceNotFoundException if the user no longer exists
     */
    @GetMapping
    public UserDetailResponse getProfile(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        UUID userId = JwtUtil.getUserIdFromToken(token);
        return userService.getProfile(userId);
    }

    /**
     * Updates the profile information of the currently authenticated user.
     *
     * <p>Only the fields provided in the request are updated (partial update).</p>
     *
     * @param authorization the JWT bearer token from the {@code Authorization} request header
     * @param request       the validated request body containing the updated profile fields
     * @return the updated {@link UserDetailResponse} reflecting the new profile data
     * @throws IllegalTokenException if the token is missing, malformed, expired, or invalid
     * @throws com.secondhand.exception.ResourceNotFoundException if the user no longer exists
     * @throws com.secondhand.exception.ResourceAlreadyExistsException if the new email is already taken by another user
     */
    @PutMapping("update-profile")
    public UserDetailResponse updateProfile(@RequestHeader("Authorization") String authorization,
                                            @Valid @RequestBody UserUpdateRequest request) {
        String token = extractToken(authorization);
        UUID userId = JwtUtil.getUserIdFromToken(token);
        return userService.updateProfile(userId, request);
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * <p>Verifies that the provided current password matches the stored hash before applying the change.</p>
     *
     * @param authorization the JWT bearer token from the {@code Authorization} request header
     * @param request       the validated request body containing the current and new passwords
     * @return a success message indicating the password has been changed
     * @throws IllegalTokenException if the token is missing, malformed, expired, or invalid
     * @throws com.secondhand.exception.ResourceNotFoundException if the user no longer exists
     * @throws com.secondhand.exception.BadRequestException if the current password is incorrect
     */
    @PutMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authorization,
                                                 @Valid @RequestBody UserChangePasswordRequest request) {
        String token = extractToken(authorization);
        UUID userId = JwtUtil.getUserIdFromToken(token);
        userService.changePassword(userId, request);
        return ResponseEntity.ok("رمز عبور با موفقیت تغییر کرد");
    }

    /**
     * Permanently deletes the account of the currently authenticated user.
     *
     * <p>This is a soft delete: the user's status is set to {@code DELETED} and the account
     * cannot be used for login. The data remains in the database for auditing purposes.</p>
     *
     * @param authorization the JWT bearer token from the {@code Authorization} request header
     * @throws IllegalTokenException if the token is missing, malformed, expired, or invalid
     * @throws com.secondhand.exception.ResourceNotFoundException if the user no longer exists
     */
    @DeleteMapping("delete-account")
    public void deleteProfile(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        UUID userId = JwtUtil.getUserIdFromToken(token);
        userService.deleteProfile(userId);
    }
}