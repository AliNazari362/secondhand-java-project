package com.secondhand.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for submitting a new comment on an advertisement.
 *
 * <p>Submitted via POST /api/advertisements/{advId}/comments. The authenticated user
 * is derived from the security context by the service layer; it is not included here.</p>
 *
 * @param text the textual body of the comment
 * @param rate a 1–5 stars rating for the advertisement
 */
public record CommentRequest(

        @NotBlank(message = "متن نظر نمی‌تواند خالی باشد")
        @Size(max = 2000, message = "متن نظر نباید از ۲۰۰۰ کاراکتر بیشتر باشد")
        String text,

        @Min(value = 1, message = "امتیاز حداقل باید ۱ باشد")
        @Max(value = 5, message = "امتیاز حداکثر می‌تواند ۵ باشد")
        int rate

) {}