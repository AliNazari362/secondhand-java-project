package dto.adv;

import dto.option.OptionRequest;
import entity.Service.ServiceType;
import entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating a new service advertisement.
 *
 * <p>Submitted via POST /api/advertisements/services. The authenticated user is
 * derived from the security context by the service layer and is not included here.</p>
 *
 * @param fullName        advertisement headline; mandatory
 * @param description     free-text description; optional
 * @param city            city where the service is offered; optional
 * @param address         detailed location address; optional
 * @param specialCategory free-text sub-category of the service; optional
 * @param costOfPart      price per billing unit in Iranian Tomans; mandatory, must be &gt;= 0
 * @param typeOfPart      billing period/unit (HOURLY, DAILY, etc.); optional
 * @param options         list of key-value attribute pairs; optional
 */
public record ServiceCreateRequest(

        /** Advertisement headline shown in listings and search results; must not be blank. */
        @NotBlank(message = "Advertisement title must not be blank")
        @Size(max = 255, message = "Advertisement title must not exceed 255 characters")
        String fullName,

        /** Free-text description of the service offered; optional but recommended. */
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        /** City where the service is offered; used for geographic filtering. */
        City city,

        /** Optional detailed address or service area; provides more precision than city alone. */
        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        /** Free-text sub-category of the service (e.g., "Plumbing", "Web Design", "Tutoring"). */
        @Size(max = 150, message = "Service category must not exceed 150 characters")
        String specialCategory,

        /** Price per billing unit in Iranian Tomans; must be zero or positive. */
        @NotNull(message = "Cost of part must not be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Cost of part must be zero or positive")
        BigDecimal costOfPart,

        /** Billing period/unit that defines how costOfPart is applied to the client. */
        ServiceType typeOfPart,

        /** Optional list of additional key-value attributes describing the service. */
        @Valid
        List<OptionRequest> options

) {}
