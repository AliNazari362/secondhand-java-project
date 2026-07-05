package dto.comment;

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
 * @param rate a 1–5 star rating for the advertisement
 */
public record CommentRequest(

        /** The body of the comment written by the user; must not be blank. */
        @NotBlank(message = "Comment text must not be blank")
        @Size(max = 2000, message = "Comment text must not exceed 2000 characters")
        String text,

        /** Star rating on a 1 (worst) to 5 (best) scale. */
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        int rate

) {}
