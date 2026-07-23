package com.secondhand.service;

import com.secondhand.dto.user.*;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing user profile operations.
 * <p>
 * Provides functionality for retrieving and updating user profiles, changing passwords,
 * and soft-deleting accounts. Also exposes shared utilities used by other services
 * for user lookups and DTO conversion.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructs a {@code UserService} with the required repository dependency.
     *
     * @param userRepository the repository for persisting and querying {@link User} entities
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the full profile of the authenticated user.
     *
     * @param userId the UUID of the user whose profile should be retrieved
     * @return a {@link UserDetailResponse} containing the user's profile data
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public UserDetailResponse getProfile(UUID userId) {
        User user = findUserById(userId);
        return toUserDetailResponse(user);
    }

    /**
     * Updates the profile fields of an existing user.
     * <p>
     * Only non-{@code null} fields in the request are applied. If a new email is provided,
     * it is checked for uniqueness against other accounts (the user's own current email
     * is exempt from the uniqueness check).
     * </p>
     *
     * @param userId  the UUID of the user whose profile should be updated
     * @param request the update data; fields set to {@code null} are not changed
     * @return a {@link UserDetailResponse} reflecting the updated profile
     * @throws ResourceNotFoundException      if no user exists with the given {@code userId}
     * @throws ResourceAlreadyExistsException if the new email is already in use by another account
     */
    public UserDetailResponse updateProfile(UUID userId, UserUpdateRequest request) {
        User user = findUserById(userId);

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.email() != null) {
            if (userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email())) {
                throw new ResourceAlreadyExistsException("این ایمیل پیش از این در سیستم ثبت شده است");
            }
            user.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        User updated = userRepository.save(user);
        return toUserDetailResponse(updated);
    }

    /**
     * Changes the password for an existing user.
     * <p>
     * Verifies that the provided current password matches the stored hash before
     * hashing and persisting the new password.
     * </p>
     *
     * @param userId  the UUID of the user whose password should be changed
     * @param request the password change data containing the current and new passwords
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     * @throws BadRequestException       if the provided current password is incorrect
     */
    public void changePassword(UUID userId, UserChangePasswordRequest request) {
        User user = findUserById(userId);

        if (!PasswordUtil.verifyPassword(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("رمز عبور فعلی شما نادرست وارد شده است");
        }

        user.setPassword(PasswordUtil.hashPassword(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Soft-deletes the user's own account by setting its status to {@link UserStatus#DELETED}.
     * <p>
     * The account record is retained in the database but the user will no longer be
     * able to log in (enforced by {@link AuthService#login}).
     * </p>
     *
     * @param userId the UUID of the user whose account should be deleted
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public void deleteProfile(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    /**
     * Looks up a user by their UUID, throwing if not found.
     * <p>
     * This method is used as a shared lookup utility across multiple services.
     * </p>
     *
     * @param userId the UUID of the user to find
     * @return the {@link User} entity with the given ID
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("کاربری با این مشخصات یافت نشد"));
    }

    /**
     * Persists a {@link User} entity.
     * <p>
     * Used by other services (e.g., {@link FavoriteService}) that need to save user
     * state without performing a full profile update.
     * </p>
     *
     * @param user the user entity to save; must not be {@code null}
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Converts a {@link User} entity to a full {@link UserDetailResponse} DTO.
     *
     * @param user the user entity to convert
     * @return a {@link UserDetailResponse} containing all profile fields
     */
    public UserDetailResponse toUserDetailResponse(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.getUserStatus()
        );
    }

    /**
     * Converts a {@link User} entity to a lightweight {@link UserSummaryResponse} DTO.
     * <p>
     * Used in contexts where only identifying information is needed
     * (e.g., advertisement owner, comment author).
     * </p>
     *
     * @param user the user entity to convert
     * @return a {@link UserSummaryResponse} containing id, name, email, and user type
     */
    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType()
        );
    }
}