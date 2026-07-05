package dto.adv;

import entity.enums.AdvStatus;
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
        @NotNull(message = "Status must not be null")
        AdvStatus status,

        /** Admin-written explanation for the rejection; recommended when status is REJECTED. */
        @Size(max = 2000, message = "Rejection explanation must not exceed 2000 characters")
        String rejectionExplanation

) {}
