package model.response;

import model.enums.ProductState;
import java.math.BigDecimal;

public class ProductDetailDto {
    private ProductState stateOfProduct;
    private String brand;
    private String model;
    private String constructor;
    private String categoryName;  // <-- تغییر: از Category به String
    private BigDecimal price;

    public ProductDetailDto() {}

    // Getters and Setters
    public ProductState getStateOfProduct() { return stateOfProduct; }
    public void setStateOfProduct(ProductState stateOfProduct) { this.stateOfProduct = stateOfProduct; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getConstructor() { return constructor; }
    public void setConstructor(String constructor) { this.constructor = constructor; }

    public String getCategoryName() { return categoryName; }  // <-- تغییر
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }  // <-- تغییر

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}