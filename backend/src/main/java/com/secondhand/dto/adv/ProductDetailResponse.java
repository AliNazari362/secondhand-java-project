package com.secondhand.dto.adv;

import com.secondhand.entity.Product.ProductState;
import java.math.BigDecimal;

/**
 * Embedded response DTO carrying the product-specific fields of a product advertisement.
 *
 * <p>Nested inside {@link AdvDetailResponse} when {@code advType} is PRODUCT.
 * This approach avoids creating a separate product detail endpoint while keeping the
 * DTO graph free of inheritance and circular references.</p>
 *
 * @param stateOfProduct physical condition of the product
 * @param brand          brand name of the product
 * @param model          model name or number of the product
 * @param constructor    manufacturer name of the product
 * @param categoryName   name of the product category
 * @param price          asking price in Iranian Tomans
 */
public record ProductDetailResponse(
        ProductState stateOfProduct,
        String brand,
        String model,
        String constructor,
        String categoryName,
        BigDecimal price
) {}