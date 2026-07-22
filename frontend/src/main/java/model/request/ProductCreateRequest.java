package model.request;

import model.enums.City;
import model.enums.ProductState;
import java.math.BigDecimal;
import java.util.List;

public class ProductCreateRequest {
    private String fullName;
    private String description;
    private City city;
    private String address;
    private ProductState stateOfProduct;
    private String brand;
    private String model;
    private String constructor;
    private Long categoryId;  // <-- تغییر: از Category به Long
    private BigDecimal price;
    private List<OptionRequest> options;
    private List<ImageRequest> images;  // <-- اضافه شد (قبلاً نبود)

    public ProductCreateRequest() {}

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public ProductState getStateOfProduct() { return stateOfProduct; }
    public void setStateOfProduct(ProductState stateOfProduct) { this.stateOfProduct = stateOfProduct; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getConstructor() { return constructor; }
    public void setConstructor(String constructor) { this.constructor = constructor; }

    public Long getCategoryId() { return categoryId; }  // <-- تغییر
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }  // <-- تغییر

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public List<OptionRequest> getOptions() { return options; }
    public void setOptions(List<OptionRequest> options) { this.options = options; }

    public List<ImageRequest> getImages() { return images; }  // <-- جدید
    public void setImages(List<ImageRequest> images) { this.images = images; }  // <-- جدید
}