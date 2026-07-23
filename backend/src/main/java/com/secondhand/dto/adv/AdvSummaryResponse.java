package com.secondhand.dto.adv;

import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import java.math.BigDecimal;
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
 * @param categoryName   name of the category that classifies this advertisement
 * @param price          asking price for product advertisements, or null for service advertisements
 */
public record AdvSummaryResponse(
        UUID id,
        String fullName,
        AdvType advType,
        AdvStatus status,
        City city,
        String ownerFullName,
        UUID ownerId,
        LocalDateTime creationDate,
        String firstImagePath,
        String categoryName,
        BigDecimal price
) {}