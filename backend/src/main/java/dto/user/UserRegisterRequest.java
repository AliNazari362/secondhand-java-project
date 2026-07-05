package dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for registering a new user account.
 *
 * <p>Submitted via POST /api/users/register. The password field carries the raw
 * plaintext password; the service layer is responsible for hashing it before
 * persisting the entity.</p>
 *
 * @param fullName    display name for the new account
 * @param email       unique email address to use as the login credential
 * @param password    plaintext password (will be hashed by the service layer)
 * @param phoneNumber optional mobile phone number
 */
public record UserRegisterRequest(

        /** Display name that will be shown on the user's advertisements and comments. */
        @NotBlank(message = "Full name must not be blank")
        @Size(max = 150, message = "Full name must not exceed 150 characters")
        String fullName,

        /** Email address used as the login identifier; must be unique in the system. */
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        @Size(max = 254, message = "Email must not exceed 254 characters")
        String email,

        /** Raw plaintext password chosen by the user; minimum 8 characters. */
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password,

        /** Optional mobile phone number in E.164-compatible format. */
        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Phone number must contain 7 to 15 digits, optionally prefixed with +"
        )
        String phoneNumber

) {}
