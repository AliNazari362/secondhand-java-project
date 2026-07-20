package com.secondhand.dto.adv;

import com.secondhand.dto.image.ImageRequest;
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
 * Request DTO for creating a new service advertisement.
 *
 * <p>This DTO is sent by the client (Frontend) to the server to register a new
 * service offering in the system. Services include programming, repairs, consulting,
 * and similar offerings. Pricing can be based on hourly, daily, weekly, monthly,
 * annual, or fixed rates.</p>
 *
 * <p>The authenticated user is identified via the JWT token, so the user ID is
 * not included in this request.</p>
 *
 * <p><strong>Image Support:</strong> The {@code images} field allows uploading
 * multiple image references (paths) along with the advertisement. These images
 * are stored and associated with the advertisement during creation.</p>
 *
 * @param fullName        Advertisement title (required, max 255 characters)
 * @param description     Full description (optional, max 5000 characters)
 * @param city            City where the service is offered (optional)
 * @param address         Detailed address (optional, max 500 characters)
 * @param specialCategory Free-text sub-category of the service (optional, max 150 characters)
 * @param costOfPart      Price per billing unit (required, must be zero or positive)
 * @param typeOfPart      Billing unit type (HOURLY, DAILY, WEEKLY, MONTHLY, ANNUAL, FIXED)
 * @param options         List of dynamic key-value attributes (optional)
 * @param images          List of image paths (optional)
 */
public record ServiceCreateRequest(

        @NotBlank(message = "عنوان آگهی نمی‌تواند خالی باشد")
        @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
        String fullName,

        @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String description,

        City city,

        @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String address,

        @Size(max = 150, message = "دسته‌بندی خدمات نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String specialCategory,

        @NotNull(message = "هزینه خدمات نمی‌تواند خالی باشد")
        @DecimalMin(value = "0.0", inclusive = true, message = "هزینه خدمات باید صفر یا مثبت باشد")
        BigDecimal costOfPart,

        ServiceType typeOfPart,

        @Valid
        List<OptionRequest> options,

        @Valid
        List<ImageRequest> images
) {}