package com.secondhand.dto.adv;

import com.secondhand.dto.image.ImageRequest;
import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.Product.ProductState;
import com.secondhand.entity.enums.Category;
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
 * <p>This DTO is sent by the client (Frontend) to the server to register a new
 * physical goods advertisement in the system. The authenticated user is identified
 * via the JWT token, so the user ID is not included in this request.</p>
 *
 * <p>All required fields are annotated with validation constraints. Optional fields
 * may be {@code null} and are handled appropriately on the server side.</p>
 *
 * <p><strong>Image Support:</strong> The {@code images} field allows uploading
 * multiple image references (paths) along with the advertisement. These images
 * are stored and associated with the advertisement during creation.</p>
 *
 * @param fullName       Advertisement title (required, max 255 characters)
 * @param description    Full description (optional, max 5000 characters)
 * @param city           City where the product is located (optional)
 * @param address        Detailed street address (optional, max 500 characters)
 * @param stateOfProduct Physical condition of the product (NEW, LIKE_NEW, GOOD, FAIR, DAMAGED, REFURBISHED)
 * @param brand          Brand name (optional, max 100 characters)
 * @param model          Model name or number (optional, max 150 characters)
 * @param constructor    Manufacturer name (optional, max 150 characters)
 * @param category       Product category (optional)
 * @param price          Asking price in Iranian Tomans (required, must be zero or positive)
 * @param options        List of dynamic key-value attributes (optional)
 * @param images         List of image paths (optional)
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

        Category category,

        @NotNull(message = "قیمت نمی‌تواند خالی باشد")
        @PositiveOrZero(message = "قیمت باید صفر یا مثبت باشد")
        BigDecimal price,

        @Valid
        List<OptionRequest> options,

        @Valid
        List<ImageRequest> images
) {}