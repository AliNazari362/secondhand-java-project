package model.request;

import model.enums.City;
import model.enums.ServiceType;
import java.math.BigDecimal;
import java.util.List;

public class ServiceCreateRequest {
    private String fullName;
    private String description;
    private City city;
    private String address;
    private String specialCategory;
    private Long categoryId;  // <-- جدید
    private BigDecimal costOfPart;
    private ServiceType typeOfPart;
    private List<OptionRequest> options;
    private List<ImageRequest> images;  // <-- جدید

    public ServiceCreateRequest() {}

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSpecialCategory() { return specialCategory; }
    public void setSpecialCategory(String specialCategory) { this.specialCategory = specialCategory; }

    public Long getCategoryId() { return categoryId; }  // <-- جدید
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }  // <-- جدید

    public BigDecimal getCostOfPart() { return costOfPart; }
    public void setCostOfPart(BigDecimal costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }
    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }

    public List<OptionRequest> getOptions() { return options; }
    public void setOptions(List<OptionRequest> options) { this.options = options; }

    public List<ImageRequest> getImages() { return images; }  // <-- جدید
    public void setImages(List<ImageRequest> images) { this.images = images; }  // <-- جدید
}