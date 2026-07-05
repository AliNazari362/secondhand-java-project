package dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for adding a new image path to an advertisement.
 *
 * <p>The actual file upload is handled separately (e.g., multipart form-data).
 * This DTO carries the resulting storage path or key after the file has been saved.</p>
 *
 * @param path relative filesystem path or object-storage key for the uploaded image
 */
public record ImageRequest(

        /** Relative path or storage key for the image file; must not be blank. */
        @NotBlank(message = "Image path must not be blank")
        @Size(max = 500, message = "Image path must not exceed 500 characters")
        String path

) {}
