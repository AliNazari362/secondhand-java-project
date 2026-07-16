package DTO.adv;

import DTO.option.OptionRequest;
import Entity.Product.ProductState;
import Entity.enums.Category;
import Entity.enums.City;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating an existing product advertisement.
 *
 * <p>Submitted via PUT /api/advertisements/products/{id}. All fields are optional;
 * only non-null fields provided by the client should be applied by the service layer
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
        @Size(max = 255, message = "Advertisement title must not exceed 255 characters")
        String fullName,

        /** New free-text description; leave null to keep the current value. */
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        /** New city location; leave null to keep the current value. */
        City city,

        /** New detailed address; leave null to keep the current value. */
        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        /** New physical condition of the product; leave null to keep the current value. */
        ProductState stateOfProduct,

        /** New brand name; leave null to keep the current value. */
        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,

        /** New model name or number; leave null to keep the current value. */
        @Size(max = 150, message = "Model must not exceed 150 characters")
        String model,

        /** New manufacturer name; leave null to keep the current value. */
        @Size(max = 150, message = "Manufacturer name must not exceed 150 characters")
        String constructor,

        /** New product category; leave null to keep the current value. */
        Category category,

        /** New asking price in Iranian Tomans; must be zero or positive. Leave null to keep current. */
        @PositiveOrZero(message = "Price must be zero or positive")
        BigDecimal price,

        /** Replacement list of key-value attributes; leave null to keep the current options. */
        @Valid
        List<OptionRequest> options

) {}
