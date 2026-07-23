package com.secondhand.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for logging a new user account.
 *
 * <p>Submitted via POST /api/users/login. The password field carries the raw
 * plaintext password; the service layer is responsible for hashing it before
 * persisting the entity.</p>
 *
 * @param email       unique email address to use as the login credential
 * @param password    plaintext password (will be hashed by the service layer)
 */
public record UserLoginRequest(

        /** Email address used as the login identifier; must be unique in the system. */
        @NotBlank(message = "ایمیل نمی‌تواند خالی باشد")
        @Email(message = "ایمیل وارد شده معتبر نیست")
        @Size(max = 254, message = "ایمیل نباید از ۲۵۴ کاراکتر بیشتر باشد")
        String email,

        /** Raw plaintext password chosen by the user; minimum 8 characters. */
        @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
        @Size(min = 8, max = 72, message = "رمز عبور باید بین ۸ تا ۷۲ کاراکتر باشد")
        String password

) {}