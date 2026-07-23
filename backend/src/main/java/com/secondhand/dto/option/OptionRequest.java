package com.secondhand.dto.option;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a single key-value attribute (option)
 * on an advertisement.
 *
 * <p>Used inline inside advertisement create/update request bodies to submit
 * structured metadata such as "RAM = 8 GB" or "Color = Black".</p>
 *
 * @param option the attribute key name (e.g., "RAM", "Color")
 * @param value  the attribute value (e.g., "8 GB", "Black")
 */
public record OptionRequest(

        @NotBlank(message = "نام گزینه نمی‌تواند خالی باشد")
        @Size(max = 150, message = "نام گزینه نباید از ۱۵۰ کاراکتر بیشتر باشد")
        String option,

        @NotBlank(message = "مقدار گزینه نمی‌تواند خالی باشد")
        @Size(max = 500, message = "مقدار گزینه نباید از ۵۰۰ کاراکتر بیشتر باشد")
        String value

) {}