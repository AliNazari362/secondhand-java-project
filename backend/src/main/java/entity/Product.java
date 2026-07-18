package entity;

import entity.enums.AdvType;
import entity.enums.Category;
import entity.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

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

    public enum ProductState {
        NEW, LIKE_NEW, GOOD, FAIR, DAMAGED, REFURBISHED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "state_of_product", length = 15)
    private ProductState stateOfProduct;

    @Size(max = 100)
    @Column(name = "brand", length = 100)
    private String brand;

    @Size(max = 150)
    @Column(name = "model", length = 150)
    private String model;

    @Size(max = 150)
    @Column(name = "constructor", length = 150)
    private String constructor;

    @NotNull
    @PositiveOrZero
    @Column(name = "price", nullable = false, precision = 15, scale = 0)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private Category category;

    // ============================================================
    // ✅ سازنده‌ها
    // ============================================================

    public Product() {
        super();
        this.setAdvType(AdvType.PRODUCT); // ✅ این خط را فعال کنید
        this.price = BigDecimal.ZERO;
    }

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

    // ============================================================
    // ✅ Getter و Setterها
    // ============================================================

    public ProductState getStateOfProduct() { return stateOfProduct; }
    public void setStateOfProduct(ProductState stateOfProduct) { this.stateOfProduct = stateOfProduct; }

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