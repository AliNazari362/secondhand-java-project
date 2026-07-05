package dto.adv;

import dto.option.OptionRequest;
import entity.Service.ServiceType;
import entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating an existing service advertisement.
 *
 * <p>Submitted via PUT /api/advertisements/services/{id}. All fields are optional;
 * only non-null fields provided by the client should be applied by the service layer
 * (partial update semantics).</p>
 *
 * @param fullName        updated headline; null means no change
 * @param description     updated description; null means no change
 * @param city            updated city; null means no change
 * @param address         updated address; null means no change
 * @param specialCategory updated service sub-category; null means no change
 * @param costOfPart      updated cost per billing unit; null means no change
 * @param typeOfPart      updated billing period/unit; null means no change
 * @param options         replacement list of key-value attributes; null means no change
 */
public record ServiceUpdateRequest(

        /** New advertisement headline; leave null to keep the current value. */
        @Size(max = 255, message = "Advertisement title must not exceed 255 characters")
        String fullName,

        /** New free-text description; leave null to keep the current value. */
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        /** New city location; leave null to keep the current value. */
        City city,

        /** New detailed address; leave null to keep the current value. */
        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        /** New free-text sub-category; leave null to keep the current value. */
        @Size(max = 150, message = "Service category must not exceed 150 characters")
        String specialCategory,

        /** New price per billing unit in Iranian Tomans; must be zero or positive. Leave null to keep current. */
        @DecimalMin(value = "0.0", inclusive = true, message = "Cost of part must be zero or positive")
        BigDecimal costOfPart,

        /** New billing period/unit; leave null to keep the current value. */
        ServiceType typeOfPart,

        /** Replacement list of key-value attributes; leave null to keep the current options. */
        @Valid
        List<OptionRequest> options

) {}
