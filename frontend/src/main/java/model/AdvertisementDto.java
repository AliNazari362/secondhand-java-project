package model;

import java.util.List;
import java.util.UUID;

public class AdvertisementDto {
    private UUID id;
    private String fullName;
    private String description;
    private long price;
    private String city;
    private String status;       // PENDING, ACTIVE, REJECTED, SOLD, DELETED
    private String advType;      // PRODUCT یا SERVICE
    private UUID ownerId;
    private String ownerFullName;
    private String creationDate;
    private String firstImagePath;
    private List<OptionDto> options;
    private List<CommentDto> comments;

    public AdvertisementDto() {}

    // ---------- Getters & Setters ----------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdvType() { return advType; }
    public void setAdvType(String advType) { this.advType = advType; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public String getOwnerFullName() { return ownerFullName; }
    public void setOwnerFullName(String ownerFullName) { this.ownerFullName = ownerFullName; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getFirstImagePath() { return firstImagePath; }
    public void setFirstImagePath(String firstImagePath) { this.firstImagePath = firstImagePath; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }

    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }
}