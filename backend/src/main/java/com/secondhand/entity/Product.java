package com.secondhand.entity;

import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.Category;
import com.secondhand.entity.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Represents a physical-goods advertisement posted on the secondhand marketplace.
 *
 * <p>Extends {@link Adv} via the JOINED inheritance strategy. All shared advertisement
 * columns live in the {@code advertisements} table; the product-specific columns live
 * in the {@code products} table and are joined by the primary key.</p>
 *
 * <p>Every product has a price (in Iranian Tomans stored as {@link BigDecimal} to avoid
 * floating-point precision errors), a category, and optional provenance information
 * (brand, model, manufacturer) together with a physical-condition rating.</p>
 */
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category"),
                @Index(name = "idx_product_price",    columnList = "price"),
                @Index(name = "idx_product_brand",    columnList = "brand")
        }
)
public class Product extends Adv {

    /**
     * Describes the physical condition of the advertised product.
     * Helps buyers evaluate the item quality without an in-person inspection.
     */
    public enum ProductState {
        /** Brand-new item, never used; still in original packaging. */
        NEW,
        /** Used only a minimal amount; practically indistinguishable from new. */
        LIKE_NEW,
        /** Normal wear consistent with regular use; fully functional. */
        GOOD,
        /** Visible wear or minor defects; still functional. */
        FAIR,
        /** Significant damage; may not be fully functional. */
        DAMAGED,
        /** Professionally restored to working condition; may show cosmetic wear. */
        REFURBISHED
    }

    /**
     * Physical condition of the product being sold.
     * Allows buyers to filter by quality and set price expectations accordingly.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state_of_product", length = 15)
    private ProductState stateOfProduct;

    /**
     * Brand name of the product (e.g., Samsung, Apple, Sony).
     * Used for brand-specific search and filtering in the marketplace.
     */
    @Size(max = 100, message = "نام برند نباید از ۱۰۰ کاراکتر بیشتر باشد")
    @Column(name = "brand", length = 100)
    private String brand;

    /**
     * Model name or number of the product (e.g., Galaxy S21, iPhone 13 Pro).
     * Provides further identification precision beyond the brand alone.
     */
    @Size(max = 150, message = "نام مدل نباید از ۱۵۰ کاراکتر بیشتر باشد")
    @Column(name = "model", length = 150)
    private String model;

    /**
     * Name of the manufacturing company that produced the physical item.
     * May differ from the brand (e.g., OEM manufacturer vs. retail brand label).
     */
    @Size(max = 150, message = "نام سازنده نباید از ۱۵۰ کاراکتر بیشتر باشد")
    @Column(name = "constructor", length = 150)
    private String constructor;

    /**
     * Asking price of the product in Iranian Tomans.
     * Stored as BigDecimal to guarantee monetary precision.
     * A value of zero indicates either a free item or that the price is negotiable on request.
     */
    @NotNull(message = "قیمت نمی‌تواند خالی باشد")
    @PositiveOrZero(message = "قیمت باید صفر یا مثبت باشد")
    @Column(name = "price", nullable = false, precision = 15, scale = 0)
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Top-level category that classifies the type of product.
     * Drives category-based navigation and filtering in the user interface.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private Category category;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     * Sets the advertisement type discriminator to PRODUCT and price to zero.
     */
    public Product() {
        super();
        this.setAdvType(AdvType.PRODUCT); // ✅ فعال شد
        this.price = BigDecimal.ZERO;
    }

    /**
     * Convenience constructor for creating a fully initialised product advertisement.
     *
     * @param description    free-text product description
     * @param user           the owner posting the advertisement
     * @param fullName       advertisement headline shown in listings
     * @param city           city where the product is located
     * @param stateOfProduct physical condition of the product
     * @param brand          product brand name
     * @param model          product model name or number
     * @param constructor    manufacturer name
     * @param category       product top-level category
     * @param price          asking price in Iranian Tomans
     */
    public Product(String description, User user, String fullName, City city,
                   ProductState stateOfProduct, String brand, String model,
                   String constructor, Category category, BigDecimal price) {
        super(description, AdvType.PRODUCT, user, fullName, city);
        this.stateOfProduct = stateOfProduct;
        this.brand = brand;
        this.model = model;
        this.constructor = constructor;
        this.category = category;
        this.price = price != null ? price : BigDecimal.ZERO;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public ProductState getStateOfProduct() { return stateOfProduct; }

    public void setStateOfProduct(ProductState stateOfProduct) {
        this.stateOfProduct = stateOfProduct;
    }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public String getConstructor() { return constructor; }

    public void setConstructor(String constructor) { this.constructor = constructor; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", brand='" + brand + '\'' +
                ", stateOfProduct=" + stateOfProduct +
                ", status=" + getStatus() +
                '}';
    }
}