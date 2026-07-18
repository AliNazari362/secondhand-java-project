package com.secondhand.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for logging a new user account.
 *
 * <p>Submitted via POST /api/users/login. The password field carries the raw
 * plaintext password; the com.secondhand.service layer is responsible for hashing it before
 * persisting the com.secondhand.entity.</p>
 *
 * @param email       unique email address to use as the login credential
 * @param password    plaintext password (will be hashed by the com.secondhand.service layer)
 */
public record UserLoginRequest(

        /** Email address used as the login identifier; must be unique in the system. */
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        @Size(max = 254, message = "Email must not exceed 254 characters")
        String email,

        /** Raw plaintext password chosen by the user; minimum 8 characters. */
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password

) {}
