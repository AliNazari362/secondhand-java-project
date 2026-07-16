package model.request;

import model.enums.City;
import model.enums.ServiceType;

import java.math.BigDecimal;
import java.util.List;

public class ServiceUpdateRequest {
    private String fullName;
    private String description;
    private City city;
    private String address;
    private String specialCategory;
    private BigDecimal costOfPart;
    private ServiceType typeOfPart;
    private List<OptionRequest> options;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialCategory() {
        return specialCategory;
    }

    public void setSpecialCategory(String specialCategory) {
        this.specialCategory = specialCategory;
    }

    public BigDecimal getCostOfPart() {
        return costOfPart;
    }

    public void setCostOfPart(BigDecimal costOfPart) {
        this.costOfPart = costOfPart;
    }

    public ServiceType getTypeOfPart() {
        return typeOfPart;
    }

    public void setTypeOfPart(ServiceType typeOfPart) {
        this.typeOfPart = typeOfPart;
    }

    public List<OptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRequest> options) {
        this.options = options;
    }
}