package com.secondhand.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing user's profile information.
 *
 * <p>Submitted via PUT /api/users/{id}. All fields are optional; only non-null
 * fields provided by the client should be applied by the com.secondhand.service layer (partial update).</p>
 *
 * @param fullName    new display name; null means no change
 * @param email       new email address; null means no change
 * @param phoneNumber new phone number; null means no change
 */
public record UserUpdateRequest(

        /** New display name; leave null to keep the current value. */
        @Size(max = 150, message = "نام کامل نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String fullName,

        /** New email address; must be unique in the system. Leave null to keep the current value. */
        @Email(message = "ایمیل وارد شده معتبر نیست")
        @Size(max = 254, message = "ایمیل نباید از ۲۵۴ کاراکتر بیشتر باشد")
        String email,

        /** New phone number in E.164-compatible format. Leave null to keep the current value. */
        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "شماره تلفن باید ۷ تا ۱۵ رقم باشد و می‌تواند با + شروع شود"
        )
        String phoneNumber

) {}
