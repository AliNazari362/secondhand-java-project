import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Adv {
    private UUID id;
    private AdvStatus status;
    private AdvType advType;
    private String description;
    private List<Option> options;
    private String rejectionExplanation;  // توضیح رد شدن توسط مدیر
    private User user;                    // صاحب آگهی
    private LocalDateTime creationDate;
    private List<Comment> comments;
    private List<Image> images;
    private String fullName;              // عنوان کامل آگهی
    private City city;
    private String address;               // آدرس دقیق (اختیاری)

    public Adv() {
        this.id = UUID.randomUUID();
        this.options = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.images = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.status = AdvStatus.PENDING;
    }

    public Adv(String description, AdvType advType, User user, String fullName, City city) {
        this();
        this.description = description;
        this.advType = advType;
        this.user = user;
        this.fullName = fullName;
        this.city = city;
    }

    // Getter و Setter
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public AdvStatus getStatus() { return status; }
    public void setStatus(AdvStatus status) { this.status = status; }

    public AdvType getAdvType() { return advType; }
    public void setAdvType(AdvType advType) { this.advType = advType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }
    public void addOption(Option option) { this.options.add(option); }

    public String getRejectionExplanation() { return rejectionExplanation; }
    public void setRejectionExplanation(String rejectionExplanation) { this.rejectionExplanation = rejectionExplanation; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void addComment(Comment comment) { this.comments.add(comment); }

    public List<Image> getImages() { return images; }
    public void setImages(List<Image> images) { this.images = images; }
    public void addImage(Image image) { this.images.add(image); }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Adv{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                ", advType=" + advType +
                ", city=" + city +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", creationDate=" + creationDate +
                '}';
    }
}