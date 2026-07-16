package DTO.image;

/**
 * Response DTO representing an image attached to an advertisement.
 *
 * <p>Returned as part of advertisement detail responses; the client uses the
 * {@code path} field to construct a URL or fetch the image from storage.</p>
 *
 * @param id   the surrogate identifier of the image record
 * @param path relative filesystem path or object-storage key for the image file
 */
public record ImageResponse(

        /** Surrogate database identifier for this image. */
        Long id,

        /** Relative path or storage key pointing to the actual image file. */
        String path

) {}
