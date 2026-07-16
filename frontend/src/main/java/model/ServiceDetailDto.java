package model;

import model.enums.ServiceType;

import java.math.BigDecimal;

public class ServiceDetailDto {
    private String specialCategory;
    private BigDecimal costOfPart;
    private ServiceType typeOfPart;

    public ServiceDetailDto() {}

    public String getSpecialCategory() { return specialCategory; }
    public void setSpecialCategory(String specialCategory) { this.specialCategory = specialCategory; }

    public BigDecimal getCostOfPart() { return costOfPart; }
    public void setCostOfPart(BigDecimal costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }
    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }
}