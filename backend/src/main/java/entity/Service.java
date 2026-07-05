package entity;

import entity.enums.AdvType;
import entity.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Represents a service-offering advertisement posted on the secondhand marketplace.
 *
 * <p>Extends {@link Adv} via the JOINED inheritance strategy. All shared advertisement
 * columns live in the {@code advertisements} table; the service-specific columns live
 * in the {@code services} table and are joined by the primary key.</p>
 *
 * <p>A service has a free-text category, a billing unit ({@link ServiceType}), and the
 * cost per that unit. For example: "Plumbing — 500,000 Tomans per HOUR".</p>
 */
@Entity
@Table(
        name = "services",
        indexes = {
                @Index(name = "idx_service_special_category", columnList = "special_category"),
                @Index(name = "idx_service_type_of_part",     columnList = "type_of_part"),
                @Index(name = "idx_service_cost",             columnList = "cost_of_part")
        }
)
public class Service extends Adv {

    /**
     * Billing period / pricing unit for a service offering.
     * Defines how the {@code costOfPart} field should be interpreted by the buyer.
     */
    public enum ServiceType {
        /** Price applies per hour of work. */
        HOURLY,
        /** Price applies per day of work. */
        DAILY,
        /** Price applies per week of engagement. */
        WEEKLY,
        /** Price applies per calendar month. */
        MONTHLY,
        /** Price applies per year / annual contract. */
        ANNUAL,
        /** A single fixed price for the entire service, regardless of duration. */
        FIXED
    }

    /**
     * Free-text sub-category for the service (e.g., "Plumbing", "Web Design", "Tutoring").
     * More granular than the platform-level category; defined by the service provider.
     */
    @Size(max = 150, message = "Service category must not exceed 150 characters")
    @Column(name = "special_category", length = 150)
    private String specialCategory;

    /**
     * Price per billing unit as defined by {@link #typeOfPart}, in Iranian Tomans.
     * Stored as BigDecimal to guarantee monetary precision.
     * Must be zero or positive (zero may indicate negotiable pricing).
     */
    @NotNull(message = "Cost of part must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost of part must be zero or positive")
    @Column(name = "cost_of_part", nullable = false, precision = 15, scale = 2)
    private BigDecimal costOfPart = BigDecimal.ZERO;

    /**
     * Billing unit for the service (HOURLY, DAILY, WEEKLY, MONTHLY, ANNUAL, or FIXED).
     * Together with {@link #costOfPart} it communicates the pricing model to potential clients.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_part", length = 10)
    private ServiceType typeOfPart;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     * Sets the advertisement type discriminator to SERVICE and cost to zero.
     */
    public Service() {
        super();
        this.setAdvType(AdvType.SERVICE);
        this.costOfPart = BigDecimal.ZERO;
    }

    /**
     * Convenience constructor for creating a fully initialised service advertisement.
     *
     * @param description     free-text description of the service
     * @param user            the owner/provider posting the advertisement
     * @param fullName        advertisement headline shown in listings
     * @param city            city where the service is offered
     * @param specialCategory free-text sub-category of the service
     * @param costOfPart      price per billing unit in Iranian Tomans
     * @param typeOfPart      billing period / pricing unit
     */
    public Service(String description, User user, String fullName, City city,
                   String specialCategory, BigDecimal costOfPart, ServiceType typeOfPart) {
        super(description, AdvType.SERVICE, user, fullName, city);
        this.specialCategory = specialCategory;
        this.costOfPart = costOfPart != null ? costOfPart : BigDecimal.ZERO;
        this.typeOfPart = typeOfPart;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getSpecialCategory() { return specialCategory; }

    public void setSpecialCategory(String specialCategory) {
        this.specialCategory = specialCategory;
    }

    public BigDecimal getCostOfPart() { return costOfPart; }

    public void setCostOfPart(BigDecimal costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }

    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "Service{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", specialCategory='" + specialCategory + '\'' +
                ", costOfPart=" + costOfPart +
                ", typeOfPart=" + typeOfPart +
                ", status=" + getStatus() +
                '}';
    }
}
