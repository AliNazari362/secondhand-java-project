package com.secondhand.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing comment on an advertisement.
 *
 * <p>Submitted via PUT /api/advertisements/{advId}/comments/{commentId}.
 * Both fields are required; partial updates are not supported for comments.</p>
 *
 * @param text the updated body of the comment
 * @param rate the updated 1–5 star rating
 */
public record CommentUpdateRequest(

        /** The updated body of the comment; must not be blank. */
        @NotBlank(message = "متن نظر نمی‌تواند خالی باشد")
        @Size(max = 2000, message = "متن نظر نباید از ۲۰۰۰ کاراکتر بیشتر باشد")
        String text,

        /** Updated star rating on a 1 (worst) to 5 (best) scale. */
        @Min(value = 1, message = "امتیاز حداقل باید ۱ باشد")
        @Max(value = 5, message = "امتیاز حداکثر می‌تواند ۵ باشد")
        int rate

) {}
