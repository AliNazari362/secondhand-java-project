package com.secondhand.dto.adv;

import com.secondhand.entity.Product.ProductState;
import java.math.BigDecimal;

/**
 * Embedded response DTO carrying the product-specific fields of a product advertisement.
 *
 * <p>Nested inside {@link AdvDetailResponse}
 * type is PRODUCT. This approach avoids creating a separate product detail endpoint
 * while keeping the DTO graph free of inheritance and circular references.</p>
 *
 * @param stateOfProduct physical condition of the product
 * @param brand          brand name of the product
 * @param model          model name or number of the product
 * @param constructor    manufacturer name of the product
 * @param categoryName   name of the product category (derived from the new {@link com.secondhand.entity.Category} entity)
 * @param price          asking price in Iranian Tomans
 */
public record ProductDetailResponse(
        /** Physical condition of the product (NEW, LIKE_NEW, GOOD, FAIR, DAMAGED, REFURBISHED). */
        ProductState stateOfProduct,

        /** Brand name of the product (e.g., Samsung, Apple, Sony). */
        String brand,

        /** Model name or number of the product (e.g., Galaxy S21, iPhone 13 Pro). */
        String model,

        /** Name of the manufacturing company that produced the item. */
        String constructor,

        /** Top-level category name that classifies the type of product. */
        String categoryName,  // <-- تغییر: به جای Category category

        /** Asking price in Iranian Tomans; zero indicates free or price-on-request. */
        BigDecimal price
) {}