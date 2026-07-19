package com.secondhand.dto.adv;

import com.secondhand.entity.enums.AdvStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating the lifecycle status of an advertisement.
 *
 * <p>Used by admin endpoints (e.g., POST /api/advertisements/{id}/status) to approve,
 * reject, or otherwise transition an advertisement through its lifecycle. If the new
 * status is REJECTED, a {@code rejectionExplanation} should be provided to inform
 * the owner of the reason.</p>
 *
 * @param status               the new lifecycle status to apply; mandatory
 * @param rejectionExplanation optional explanation; should be set when status is REJECTED
 */
public record AdvStatusUpdateRequest(

        /** The target lifecycle status to transition the advertisement into. */
        @NotNull(message = "وضعیت آگهی نمی‌تواند خالی باشد")
        AdvStatus status,

        /** Admin-written explanation for the rejection; recommended when status is REJECTED. */
        @Size(max = 2000, message = "توضیحات رد آگهی نباید از ۲۰۰۰ کاراکتر بیشتر باشد")
        String rejectionExplanation

) {}
