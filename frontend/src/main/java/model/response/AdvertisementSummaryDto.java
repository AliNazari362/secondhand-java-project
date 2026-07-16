package model.response;

import model.enums.AdvStatus;
import model.enums.AdvType;
import model.enums.City;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public AdvertisementSummaryDto() {}

    // Getters & Setters
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
}