package com.secondhand.dto.adv;

import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.Service.ServiceType;
import com.secondhand.entity.enums.City;
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
 * @param categoryId      updated category ID; null means no change
 * @param costOfPart      updated cost per billing unit; null means no change
 * @param typeOfPart      updated billing period/unit; null means no change
 * @param options         replacement list of key-value attributes; null means no change
 */
public record ServiceUpdateRequest(
        @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
        String fullName,

        @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String description,

        City city,

        @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String address,

        @Size(max = 150, message = "دسته‌بندی خدمات نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String specialCategory,

        Long categoryId,

        @DecimalMin(value = "0.0", inclusive = true, message = "هزینه خدمات باید صفر یا مثبت باشد")
        BigDecimal costOfPart,

        ServiceType typeOfPart,

        @Valid
        List<OptionRequest> options
) {}