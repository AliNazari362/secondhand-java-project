package DTO.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for changing an authenticated user's password.
 *
 * <p>Submitted via POST /api/users/{id}/change-password. The service layer must verify
 * that {@code currentPassword} matches the stored hash before applying the change.</p>
 *
 * @param currentPassword the user's current plaintext password for verification
 * @param newPassword     the new plaintext password to set (will be hashed by the service layer)
 */
public record UserChangePasswordRequest(

        /** Current plaintext password used to verify the user's identity before the change. */
        @NotBlank(message = "Current password must not be blank")
        String currentPassword,

        /** New plaintext password; minimum 8 characters. Will be hashed before storage. */
        @NotBlank(message = "New password must not be blank")
        @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters")
        String newPassword

) {}
