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
        @NotBlank(message = "Comment text must not be blank")
        @Size(max = 2000, message = "Comment text must not exceed 2000 characters")
        String text,

        /** Updated star rating on a 1 (worst) to 5 (best) scale. */
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        int rate

) {}
