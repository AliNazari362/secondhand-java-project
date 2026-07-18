package com.secondhand.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for changing an authenticated user's password.
 *
 * <p>Submitted via POST /api/users/{id}/change-password. The com.secondhand.service layer must verify
 * that {@code currentPassword} matches the stored hash before applying the change.</p>
 *
 * @param currentPassword the user's current plaintext password for verification
 * @param newPassword     the new plaintext password to set (will be hashed by the com.secondhand.service layer)
 */
public record UserChangePasswordRequest(

        /** Current plaintext password used to verify the user's identity before the change. */
        @NotBlank(message = "رمز عبور فعلی نمی‌تواند خالی باشد")
        String currentPassword,

        /** New plaintext password; minimum 8 characters. Will be hashed before storage. */
        @NotBlank(message = "رمز عبور جدید نمی‌تواند خالی باشد")
        @Size(min = 8, max = 72, message = "رمز عبور جدید باید بین ۸ تا ۷۲ کاراکتر باشد")
        String newPassword

) {}
