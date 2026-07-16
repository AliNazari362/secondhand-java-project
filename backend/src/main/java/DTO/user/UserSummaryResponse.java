package DTO.user;

import Entity.enums.UserType;

import java.util.UUID;

/**
 * Lightweight response DTO representing a user in contexts where full detail is unnecessary.
 *
 * <p>Used inside advertisement, comment, and message responses to identify the associated
 * user without embedding the complete user profile. Deliberately excludes sensitive fields
 * such as password, phone number, and account status.</p>
 *
 * @param id       the unique identifier of the user
 * @param fullName the display name of the user
 * @param email    the public email address of the user
 * @param userType the role of the user (USER or ADMIN)
 */
public record UserSummaryResponse(

        /** Unique identifier of the user account. */
        UUID id,

        /** Human-readable display name shown alongside advertisements and comments. */
        String fullName,

        /** Email address used as the unique login credential and contact identifier. */
        String email,

        /** Role indicating whether this person is a regular user or a platform admin. */
        UserType userType

) {}
