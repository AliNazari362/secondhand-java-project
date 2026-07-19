package com.secondhand.dto.adv;

import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.Product.ProductState;
import com.secondhand.entity.enums.Category;
import com.secondhand.entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating an existing product advertisement.
 *
 * <p>Submitted via PUT /api/advertisements/products/{id}. All fields are optional;
 * only non-null fields provided by the client should be applied by the com.secondhand.service layer
 * (partial update semantics).</p>
 *
 * @param fullName       updated headline; null means no change
 * @param description    updated description; null means no change
 * @param city           updated city; null means no change
 * @param address        updated address; null means no change
 * @param stateOfProduct updated physical condition; null means no change
 * @param brand          updated brand; null means no change
 * @param model          updated model; null means no change
 * @param constructor    updated manufacturer; null means no change
 * @param category       updated category; null means no change
 * @param price          updated price; null means no change
 * @param options        replacement list of key-value attributes; null means no change
 */
public record ProductUpdateRequest(

        /** New advertisement headline; leave null to keep the current value. */
        @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
        String fullName,

        /** New free-text description; leave null to keep the current value. */
        @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String description,

        /** New city location; leave null to keep the current value. */
        City city,

        /** New detailed address; leave null to keep the current value. */
        @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String address,

        /** New physical condition of the product; leave null to keep the current value. */
        ProductState stateOfProduct,

        /** New brand name; leave null to keep the current value. */
        @Size(max = 100, message = "نام برند نباید از ۱۰۰ کاراکتر بیشتر باشد")
        String brand,

        /** New model name or number; leave null to keep the current value. */
        @Size(max = 150, message = "نام مدل نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String model,

        /** New manufacturer name; leave null to keep the current value. */
        @Size(max = 150, message = "نام سازنده نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String constructor,

        /** New product category; leave null to keep the current value. */
        Category category,

        /** New asking price in Iranian Tomans; must be zero or positive. Leave null to keep current. */
        @PositiveOrZero(message = "قیمت باید صفر یا مثبت باشد")
        BigDecimal price,

        /** Replacement list of key-value attributes; leave null to keep the current options. */
        @Valid
        List<OptionRequest> options

) {}
