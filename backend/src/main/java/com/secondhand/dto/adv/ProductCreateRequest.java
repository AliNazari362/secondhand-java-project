package com.secondhand.dto.adv;

import com.secondhand.dto.image.ImageRequest;
import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.Product.ProductState;
import com.secondhand.entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating a new product advertisement.
 *
 * <p>This DTO is sent by the client to register a new physical goods advertisement.
 * The authenticated user is identified via the JWT token, so the user ID is not included.</p>
 *
 * <p>All required fields are annotated with validation constraints. Optional fields
 * may be {@code null} and are handled appropriately on the server side.</p>
 *
 * <p><strong>Image Support:</strong> The {@code images} field allows uploading
 * multiple image paths along with the advertisement.</p>
 *
 * @param fullName       advertisement title (required, max 255 chars)
 * @param description    full description (optional, max 5000 chars)
 * @param city           city where the product is located (optional)
 * @param address        detailed street address (optional, max 500 chars)
 * @param stateOfProduct physical condition of the product
 * @param brand          brand name (optional, max 100 chars)
 * @param model          model name or number (optional, max 150 chars)
 * @param constructor    manufacturer name (optional, max 150 chars)
 * @param categoryId     ID of the product category (optional)
 * @param price          asking price (required, zero or positive)
 * @param options        list of dynamic key-value attributes (optional)
 * @param images         list of image paths (optional)
 */
public record ProductCreateRequest(
        @NotBlank(message = "عنوان آگهی نمی‌تواند خالی باشد")
        @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
        String fullName,

        @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String description,

        City city,

        @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String address,

        ProductState stateOfProduct,

        @Size(max = 100, message = "نام برند نباید از ۱۰۰ کاراکتر بیشتر باشد")
        String brand,

        @Size(max = 150, message = "نام مدل نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String model,

        @Size(max = 150, message = "نام سازنده نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String constructor,

        Long categoryId,

        @NotNull(message = "قیمت نمی‌تواند خالی باشد")
        @PositiveOrZero(message = "قیمت باید صفر یا مثبت باشد")
        BigDecimal price,

        @Valid
        List<OptionRequest> options,

        @Valid
        List<ImageRequest> images
) {}