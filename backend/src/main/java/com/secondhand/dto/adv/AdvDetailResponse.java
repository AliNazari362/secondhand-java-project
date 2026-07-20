package com.secondhand.dto.adv;

import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.dto.image.ImageResponse;
import com.secondhand.dto.option.OptionResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Full-detail response DTO for a single advertisement.
 *
 * <p>Returned by GET /api/advertisements/{id}. Includes all fields from the base
 * advertisement plus the full lists of images, options (key-value attributes), and
 * user comments. The owner is embedded as a lightweight {@link UserSummaryResponse}
 * to prevent circular references.</p>
 *
 * <p>Type-specific fields for products and services are carried in the
 * {@code productDetail} and {@code serviceDetail} fields respectively; exactly one
 * of these will be non-null depending on {@code advType}.</p>
 *
 * @param id                   unique identifier of the advertisement
 * @param fullName             advertisement headline
 * @param advType              PRODUCT or SERVICE discriminator
 * @param status               current lifecycle status
 * @param description          free-text description provided by the owner
 * @param city                 city where the item or service is located
 * @param address              optional detailed address
 * @param owner                lightweight profile of the advertisement owner
 * @param creationDate         timestamp when the advertisement was first posted
 * @param lastModifiedDate     timestamp of the most recent update
 * @param rejectionExplanation admin-provided reason if status is REJECTED; null otherwise
 * @param images               all images attached to this advertisement
 * @param options              all key-value attributes describing this advertisement
 * @param comments             all user comments ordered newest-first
 * @param productDetail        product-specific fields; null if advType is SERVICE
 * @param serviceDetail        service-specific fields; null if advType is PRODUCT
 * @param categoryName         name of the category (derived from the new {@link com.secondhand.entity.Category} entity)
 */
public record AdvDetailResponse(
        /** Unique identifier of the advertisement. */
        UUID id,

        /** Headline shown in listing cards and at the top of the detail page. */
        String fullName,

        /** Discriminates whether this is a product or a service advertisement. */
        AdvType advType,

        /** Current lifecycle status (PENDING, ACTIVE, REJECTED, SOLD, DELETED). */
        AdvStatus status,

        /** Free-text description written by the owner. */
        String description,

        /** City where the advertised item or service is located. */
        City city,

        /** Optional detailed street address; may be null. */
        String address,

        /** Lightweight owner profile (id, name, email, role); no password or status. */
        UserSummaryResponse owner,

        /** Timestamp of when the advertisement was first published. */
        LocalDateTime creationDate,

        /** Timestamp of the most recent update to this advertisement. */
        LocalDateTime lastModifiedDate,

        /** Admin-written explanation for why the advertisement was rejected; null if not rejected. */
        String rejectionExplanation,

        /** All images attached to this advertisement. */
        List<ImageResponse> images,

        /** All key-value attribute pairs describing this advertisement. */
        List<OptionResponse> options,

        /** All user comments, ordered newest-first. */
        List<CommentResponse> comments,

        /** Product-specific detail fields; non-null only when advType == PRODUCT. */
        ProductDetailResponse productDetail,

        /** Service-specific detail fields; non-null only when advType == SERVICE. */
        ServiceDetailResponse serviceDetail,

        /** Name of the category that classifies this advertisement. */
        String categoryName  // <-- جدید
) {}