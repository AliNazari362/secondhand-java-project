package model.response;

import model.enums.AdvStatus;
import model.enums.AdvType;
import model.enums.City;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight DTO for displaying advertisement summaries in list/search views.
 *
 * <p>This DTO is used in the main dashboard and search results to render ad cards.
 * It contains only the essential fields needed for a card view; full details are
 * fetched separately via the detail endpoint.</p>
 *
 * <p><strong>Price Handling:</strong> The {@code price} field is populated only for
 * product advertisements. For service advertisements, it will be {@code null}.</p>
 *
 * @see model.response.AdvertisementDetailDto
 */
public class AdvertisementSummaryDto {

    private UUID id;
    private String fullName;
    private AdvType advType;
    private AdvStatus status;
    private City city;
    private String ownerFullName;
    private UUID ownerId;
    private LocalDateTime creationDate;
    private String firstImagePath;
    private String categoryName;
    private BigDecimal price; // <-- فیلد جدید برای نمایش قیمت در کارت

    /**
     * Default constructor required for JSON deserialization.
     */
    public AdvertisementSummaryDto() {}

    // ==================== Getters and Setters ====================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public AdvType getAdvType() { return advType; }
    public void setAdvType(AdvType advType) { this.advType = advType; }

    public AdvStatus getStatus() { return status; }
    public void setStatus(AdvStatus status) { this.status = status; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getOwnerFullName() { return ownerFullName; }
    public void setOwnerFullName(String ownerFullName) { this.ownerFullName = ownerFullName; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public String getFirstImagePath() { return firstImagePath; }
    public void setFirstImagePath(String firstImagePath) { this.firstImagePath = firstImagePath; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getPrice() { return price; }  // <-- جدید
    public void setPrice(BigDecimal price) { this.price = price; }  // <-- جدید
}