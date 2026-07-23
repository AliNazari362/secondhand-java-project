package com.secondhand.dto.comment;

import com.secondhand.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;

/**
 * Response DTO representing a comment and its 1–5 star rating on an advertisement.
 *
 * <p>Returned as part of advertisement detail responses and as a standalone resource
 * from GET /api/advertisements/{advId}/comments. The author is embedded as a lightweight
 * {@link UserSummaryResponse} to avoid circular references.</p>
 *
 * @param id     the surrogate identifier of the comment
 * @param text   the textual body of the comment
 * @param rate   the 1–5 stars rating
 * @param author the user who wrote the comment (summary, no sensitive data)
 * @param date   the timestamp when the comment was submitted
 */
public record CommentResponse(

        Long id,

        String text,

        int rate,

        UserSummaryResponse author,

        LocalDateTime date

) {}