package model.response;

import model.enums.ServiceType;
import java.math.BigDecimal;

public class ServiceDetailDto {
    private String specialCategory;
    private String categoryName;  // <-- جدید
    private BigDecimal costOfPart;
    private ServiceType typeOfPart;

    public ServiceDetailDto() {}

    // Getters and Setters
    public String getSpecialCategory() { return specialCategory; }
    public void setSpecialCategory(String specialCategory) { this.specialCategory = specialCategory; }

    public String getCategoryName() { return categoryName; }  // <-- جدید
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }  // <-- جدید

    public BigDecimal getCostOfPart() { return costOfPart; }
    public void setCostOfPart(BigDecimal costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }
    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }
}