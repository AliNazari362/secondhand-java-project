package entity;

import entity.enums.AdvType;
import entity.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(
        name = "services",
        indexes = {
                @Index(name = "idx_service_special_category", columnList = "special_category"),
                @Index(name = "idx_service_type_of_part",     columnList = "type_of_part"),
                @Index(name = "idx_service_cost",             columnList = "cost_of_part")
        }
)
public class ServiceObj extends Adv {

    public enum ServiceType {
        HOURLY, DAILY, WEEKLY, MONTHLY, ANNUAL, FIXED
    }

    @Size(max = 150)
    @Column(name = "special_category", length = 150)
    private String specialCategory;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "cost_of_part", nullable = false, precision = 15, scale = 2)
    private BigDecimal costOfPart = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_part", length = 10)
    private ServiceType typeOfPart;

    public ServiceObj() {
        super();
        this.setAdvType(AdvType.SERVICE);
        this.costOfPart = BigDecimal.ZERO;
    }

    public ServiceObj(String description, User user, String fullName, City city,
                      String specialCategory, BigDecimal costOfPart, ServiceType typeOfPart) {
        super(description, AdvType.SERVICE, user, fullName, city);
        this.specialCategory = specialCategory;
        this.costOfPart = costOfPart != null ? costOfPart : BigDecimal.ZERO;
        this.typeOfPart = typeOfPart;
    }

    // ============================================================
    // ✅ همه Getterها و Setterها
    // ============================================================

    public String getSpecialCategory() { return specialCategory; }
    public void setSpecialCategory(String specialCategory) { this.specialCategory = specialCategory; }

    public BigDecimal getCostOfPart() { return costOfPart; }
    public void setCostOfPart(BigDecimal costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }
    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }

    @Override
    public String toString() {
        return "ServiceObj{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", specialCategory='" + specialCategory + '\'' +
                ", costOfPart=" + costOfPart +
                ", typeOfPart=" + typeOfPart +
                ", status=" + getStatus() +
                '}';
    }
}