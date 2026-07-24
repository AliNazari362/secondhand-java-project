package com.secondhand.dto.adv;

import com.secondhand.entity.Product.ProductState;
import com.secondhand.entity.enums.Category;

import java.math.BigDecimal;

/**
 * Embedded response DTO carrying the product-specific fields of a product advertisement.
 *
 * <p>Nested inside {@link AdvDetailResponse#productDetail()} when the advertisement
 * type is PRODUCT. This approach avoids creating a separate product detail endpoint
 * while keeping the DTO graph free of inheritance and circular references.</p>
 *
 * @param stateOfProduct physical condition of the product
 * @param brand          brand name of the product
 * @param model          model name or number of the product
 * @param constructor    manufacturer name of the product
 * @param category       top-level product category
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

        /** Top-level category that classifies the type of product. */
        Category category,

        /** Asking price in Iranian Tomans; zero indicates free or price-on-request. */
        BigDecimal price

) {}
