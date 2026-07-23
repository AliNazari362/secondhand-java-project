package com.secondhand.dto.image;

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

        Long id,

        String path

) {}