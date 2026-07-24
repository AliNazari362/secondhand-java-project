package com.secondhand.dto.user;

import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;

import java.util.UUID;

/**
 * Detailed response DTO representing a user's full profile.
 *
 * <p>Returned by admin-facing endpoints and by the profile endpoint for the currently
 * authenticated user. Includes operational fields (status, phone) not present in the
 * summary DTO. Never includes the password hash.</p>
 *
 * @param id          the unique identifier of the user
 * @param fullName    the display name of the user
 * @param email       the email address / login credential
 * @param phoneNumber optional contact phone number
 * @param userType    the role of the user (USER or ADMIN)
 * @param userStatus  current account status (ACTIVE or BANNED)
 */
public record UserDetailResponse(

        /** Unique identifier of the user account. */
        UUID id,

        /** Human-readable display name shown on advertisements and comments. */
        String fullName,

        /** Email address used as the unique login credential. */
        String email,

        /** Mobile phone number in E.164-compatible format; may be null if not provided. */
        String phoneNumber,

        /** Role indicating whether this person is a regular user or a platform admin. */
        UserType userType,

        /** Current account status; BANNED users cannot log in or interact with the platform. */
        UserStatus userStatus

) {}
