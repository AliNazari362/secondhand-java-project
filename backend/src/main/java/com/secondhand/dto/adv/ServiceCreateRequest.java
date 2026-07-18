package com.secondhand.dto.adv;

import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.Service.ServiceType;
import com.secondhand.entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating a new com.secondhand.service advertisement.
 *
 * <p>Submitted via POST /api/advertisements/services. The authenticated user is
 * derived from the security context by the com.secondhand.service layer and is not included here.</p>
 *
 * @param fullName        advertisement headline; mandatory
 * @param description     free-text description; optional
 * @param city            city where the com.secondhand.service is offered; optional
 * @param address         detailed location address; optional
 * @param specialCategory free-text sub-category of the com.secondhand.service; optional
 * @param costOfPart      price per billing unit in Iranian Tomans; mandatory, must be &gt;= 0
 * @param typeOfPart      billing period/unit (HOURLY, DAILY, etc.); optional
 * @param options         list of key-value attribute pairs; optional
 */
public record ServiceCreateRequest(

        /** Advertisement headline shown in listings and search results; must not be blank. */
        @NotBlank(message = "عنوان آگهی نمی‌تواند خالی باشد")
                @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
        String fullName,

        /** Free-text description of the com.secondhand.service offered; optional but recommended. */
        @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String description,

        /** City where the com.secondhand.service is offered; used for geographic filtering. */
        City city,

        /** Optional detailed address or com.secondhand.service area; provides more precision than city alone. */
        @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String address,

        /** Free-text sub-category of the com.secondhand.service (e.g., "Plumbing", "Web Design", "Tutoring"). */
        @Size(max = 150, message = "دسته‌بندی خدمات نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String specialCategory,

        /** Price per billing unit in Iranian Tomans; must be zero or positive. */
        @NotNull(message = "هزینه خدمات نمی‌تواند خالی باشد")
        @DecimalMin(value = "0.0", inclusive = true, message = "هزینه خدمات باید صفر یا مثبت باشد")
        BigDecimal costOfPart,

        /** Billing period/unit that defines how costOfPart is applied to the client. */
        ServiceType typeOfPart,

        /** Optional list of additional key-value attributes describing the com.secondhand.service. */
        @Valid
        List<OptionRequest> options

) {}
