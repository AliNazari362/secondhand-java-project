package model.response;

import model.enums.AdvStatus;
import model.enums.AdvType;
import model.enums.City;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AdvertisementDetailDto {
    private UUID id;
    private String fullName;
    private AdvType advType;
    private AdvStatus status;
    private String description;
    private City city;
    private String address;
    private UserSummaryDto owner;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private String rejectionExplanation;
    private List<ImageResponseDto> images;
    private List<OptionResponseDto> options;
    private List<CommentResponseDto> comments;
    private ProductDetailDto productDetail;
    private ServiceDetailDto serviceDetail;
    private String categoryName;  // <-- جدید

    public AdvertisementDetailDto() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public AdvType getAdvType() { return advType; }
    public void setAdvType(AdvType advType) { this.advType = advType; }

    public AdvStatus getStatus() { return status; }
    public void setStatus(AdvStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserSummaryDto getOwner() { return owner; }
    public void setOwner(UserSummaryDto owner) { this.owner = owner; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getRejectionExplanation() { return rejectionExplanation; }
    public void setRejectionExplanation(String rejectionExplanation) { this.rejectionExplanation = rejectionExplanation; }

    public List<ImageResponseDto> getImages() { return images; }
    public void setImages(List<ImageResponseDto> images) { this.images = images; }

    public List<OptionResponseDto> getOptions() { return options; }
    public void setOptions(List<OptionResponseDto> options) { this.options = options; }

    public List<CommentResponseDto> getComments() { return comments; }
    public void setComments(List<CommentResponseDto> comments) { this.comments = comments; }

    public ProductDetailDto getProductDetail() { return productDetail; }
    public void setProductDetail(ProductDetailDto productDetail) { this.productDetail = productDetail; }

    public ServiceDetailDto getServiceDetail() { return serviceDetail; }
    public void setServiceDetail(ServiceDetailDto serviceDetail) { this.serviceDetail = serviceDetail; }

    public String getCategoryName() { return categoryName; }  // <-- جدید
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }  // <-- جدید
}