package DTO.adv;

import Entity.Service.ServiceType;

import java.math.BigDecimal;

/**
 * Embedded response DTO carrying the service-specific fields of a service advertisement.
 *
 * <p>Nested inside {@link AdvDetailResponse#serviceDetail()} when the advertisement
 * type is SERVICE. This approach avoids creating a separate service detail endpoint
 * while keeping the DTO graph free of inheritance and circular references.</p>
 *
 * @param specialCategory free-text sub-category of the service (e.g., "Plumbing", "Web Design")
 * @param costOfPart      price per billing unit in Iranian Tomans
 * @param typeOfPart      billing period/unit (HOURLY, DAILY, WEEKLY, MONTHLY, ANNUAL, FIXED)
 */
public record ServiceDetailResponse(

        /** Free-text sub-category that further classifies the type of service offered. */
        String specialCategory,

        /** Price per billing unit (defined by typeOfPart) in Iranian Tomans. */
        BigDecimal costOfPart,

        /** Billing period/unit that determines how costOfPart is applied. */
        ServiceType typeOfPart

) {}
