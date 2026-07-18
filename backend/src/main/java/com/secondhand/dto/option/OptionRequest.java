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

        /** The name of the attribute key; must not be blank. */
        @NotBlank(message = "Option name must not be blank")
        @Size(max = 150, message = "Option name must not exceed 150 characters")
        String option,

        /** The value assigned to the attribute key; must not be blank. */
        @NotBlank(message = "Option value must not be blank")
        @Size(max = 500, message = "Option value must not exceed 500 characters")
        String value

) {}
