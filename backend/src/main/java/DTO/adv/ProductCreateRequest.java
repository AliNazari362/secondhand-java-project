package DTO.adv;

import DTO.option.OptionRequest;
import Entity.Product.ProductState;
import Entity.enums.Category;
import Entity.enums.City;
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
 * <p>Submitted via POST /api/advertisements/products. The authenticated user is
 * derived from the security context by the service layer and is not included here.</p>
 *
 * @param fullName       advertisement headline; mandatory
 * @param description    free-text description; optional
 * @param city           city where the product is located; optional
 * @param address        detailed street address; optional
 * @param stateOfProduct physical condition of the product; optional
 * @param brand          product brand name; optional
 * @param model          product model name or number; optional
 * @param constructor    manufacturer name; optional
 * @param category       product top-level category; optional
 * @param price          asking price in Iranian Tomans; mandatory, must be &gt;= 0
 * @param options        list of key-value attribute pairs; optional
 */
public record ProductCreateRequest(

        /** Advertisement headline shown in listings and search results; must not be blank. */
        @NotBlank(message = "Advertisement title must not be blank")
        @Size(max = 255, message = "Advertisement title must not exceed 255 characters")
        String fullName,

        /** Free-text description of the product; optional but recommended. */
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        /** City where the product is located; used for geographic filtering. */
        City city,

        /** Optional detailed street address; provides more precision than city alone. */
        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        /** Physical condition of the product (NEW, LIKE_NEW, GOOD, FAIR, DAMAGED, REFURBISHED). */
        ProductState stateOfProduct,

        /** Brand name of the product (e.g., Samsung, Apple). */
        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,

        /** Model name or number of the product (e.g., Galaxy S21). */
        @Size(max = 150, message = "Model must not exceed 150 characters")
        String model,

        /** Name of the manufacturing company that produced the item. */
        @Size(max = 150, message = "Manufacturer name must not exceed 150 characters")
        String constructor,

        /** Top-level category that classifies the type of product. */
        Category category,

        /** Asking price in Iranian Tomans; must be zero or positive. */
        @NotNull(message = "Price must not be null")
        @PositiveOrZero(message = "Price must be zero or positive")
        BigDecimal price,

        /** Optional list of additional key-value attributes (e.g., RAM=8GB, Color=Black). */
        @Valid
        List<OptionRequest> options

) {}
