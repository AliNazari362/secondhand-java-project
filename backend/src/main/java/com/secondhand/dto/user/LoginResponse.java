package com.secondhand.dto.user;

import com.secondhand.entity.enums.UserType;

import java.util.UUID;

/**
 * Response DTO for successful login containing JWT token and user profile information.
 *
 * @param token    the JWT token for authenticated requests
 * @param userId   the unique identifier of the user
 * @param fullName the display name of the user
 * @param role     the role of the user (USER or ADMIN)
 */
public record LoginResponse(
        String token,
        UUID userId,
        String fullName,
        UserType role
) {}