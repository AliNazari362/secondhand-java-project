package DTO.adv;

import Entity.enums.AdvStatus;
import Entity.enums.AdvType;
import Entity.enums.City;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight response DTO representing an advertisement in list/search views.
 *
 * <p>Returned by paginated listing endpoints such as GET /api/advertisements.
 * Contains only the fields required to render a card in the search results; full
 * detail (images, options, comments) is loaded separately via the detail endpoint.</p>
 *
 * <p>The {@code firstImagePath} field carries the path of the advertisement's primary
 * image (if any) so the client can display a thumbnail without a second request.</p>
 *
 * @param id             unique identifier of the advertisement
 * @param fullName       advertisement headline
 * @param advType        PRODUCT or SERVICE discriminator
 * @param status         current lifecycle status
 * @param city           city where the item or service is located
 * @param ownerFullName  display name of the advertisement owner
 * @param ownerId        unique identifier of the advertisement owner
 * @param creationDate   timestamp when the advertisement was posted
 * @param firstImagePath path of the first attached image, or null if none
 */
public record AdvSummaryResponse(

        /** Unique identifier of the advertisement. */
        UUID id,

        /** Headline shown in listing cards and search results. */
        String fullName,

        /** Discriminates whether this is a product or a service advertisement. */
        AdvType advType,

        /** Current lifecycle status (PENDING, ACTIVE, REJECTED, SOLD, DELETED). */
        AdvStatus status,

        /** City where the advertised item or service is located. */
        City city,

        /** Display name of the user who posted the advertisement. */
        String ownerFullName,

        /** Unique identifier of the user who posted the advertisement. */
        UUID ownerId,

        /** Timestamp of when the advertisement was first published. */
        LocalDateTime creationDate,

        /** Relative path or storage key of the first image; null if no images are attached. */
        String firstImagePath

) {}
