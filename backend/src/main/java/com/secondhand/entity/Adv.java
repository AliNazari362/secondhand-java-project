package com.secondhand.entity;

import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base entity for all advertisements posted on the secondhand marketplace platform.
 *
 * <p>Concrete subtypes are {@link Product} (physical goods) and {@link Service} (offered services).
 * A JOINED inheritance strategy is used so that each subtype table only contains its own
 * additional columns while all shared columns reside in the {@code advertisements} table.</p>
 *
 * <p>An advertisement always belongs to exactly one {@link User} (its owner) and passes through
 * a lifecycle governed by {@link AdvStatus}: PENDING → ACTIVE | REJECTED → SOLD | DELETED.</p>
 */
@Entity
@Table(
        name = "advertisements",
        indexes = {
                @Index(name = "idx_adv_user_id", columnList = "user_id"),
                @Index(name = "idx_adv_status", columnList = "status"),
                @Index(name = "idx_adv_adv_type", columnList = "adv_type"),
                @Index(name = "idx_adv_city", columnList = "city"),
                @Index(name = "idx_adv_creation", columnList = "creation_date"),
                @Index(name = "idx_adv_category", columnList = "category_id")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Adv {

    /**
     * Globally unique identifier for this advertisement.
     * Generated once at application level and never changed.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Lifecycle status of the advertisement (PENDING, ACTIVE, REJECTED, SOLD, DELETED).
     * Defaults to PENDING upon creation; transitions are driven by admin and owner actions.
     */
    @NotNull(message = "وضعیت آگهی نمی‌تواند خالی باشد")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private AdvStatus status;

    /**
     * Discriminator indicating whether this is a PRODUCT or SERVICE advertisement.
     * Set once at construction and never modified (immutable business concept).
     */
    @NotNull(message = "نوع آگهی نمی‌تواند خالی باشد")
    @Enumerated(EnumType.STRING)
    @Column(name = "adv_type", nullable = false, updatable = false, length = 10)
    private AdvType advType;

    /**
     * Free-text description of the advertisement written by its owner.
     * Provides details that do not fit into the structured fields.
     */
    @Size(max = 5000, message = "توضیحات نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Arbitrary key-value attributes that describe the advertised item.
     * Examples: RAM=8GB, Color=Black, Engine=1600cc.
     * Owned exclusively by this advertisement; deleted when the advertisement is deleted.
     *
     * <p>This is the inverse side of the bidirectional relationship with {@link Option}.
     * The owning side is {@link Option}.</p>
     */
    @OneToMany(mappedBy = "adv", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    /**
     * Human-readable explanation provided by an admin when an advertisement is rejected.
     * Null unless the advertisement has been transitioned to REJECTED status.
     */
    @Size(max = 2000, message = "توضیحات رد آگهی نباید از ۲۰۰۰ کاراکتر بیشتر باشد")
    @Column(name = "rejection_explanation", columnDefinition = "TEXT")
    private String rejectionExplanation;

    /**
     * The user who created and owns this advertisement.
     * Every advertisement must have an owner; the association is mandatory.
     */
    @NotNull(message = "مالک آگهی نمی‌تواند خالی باشد")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_adv_user"))
    private User user;

    /**
     * Timestamp of when this advertisement was first persisted.
     * Set automatically by Hibernate and never updated.
     */
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Timestamp of the last modification to this advertisement record.
     * Updated automatically by Hibernate on every merge operation.
     */
    @UpdateTimestamp
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;

    /**
     * User-submitted comments and ratings for this advertisement.
     * Ordered from newest to oldest for display purposes.
     * Deleted automatically when the advertisement is removed.
     */
    @OneToMany(mappedBy = "adv", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date DESC")
    private List<Comment> comments = new ArrayList<>();

    /**
     * Images attached to this advertisement to visually represent the item or service.
     * Deleted automatically when the advertisement is removed.
     *
     * <p>This is the inverse side of the bidirectional relationship with {@link Image}.
     * The owning side is {@link Image}.</p>
     */
    @OneToMany(mappedBy = "adv", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    /**
     * Title/headline of the advertisement as entered by its owner.
     * Shown in listing views and search results; must be descriptive and non-empty.
     */
    @NotBlank(message = "عنوان آگهی نمی‌تواند خالی باشد")
    @Size(max = 255, message = "عنوان آگهی نباید از ۲۵۵ کاراکتر بیشتر باشد")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    /**
     * City where the advertised item or service is located.
     * Used for geographic filtering of search results.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "city", length = 20)
    private City city;

    /**
     * Optional detailed street address for the advertisement location.
     * Provides more precision than the city field alone; entered by the owner.
     */
    @Size(max = 500, message = "آدرس نباید از ۵۰۰ کاراکتر بیشتر باشد")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * The category that classifies this advertisement.
     * Helps users filter and browse advertisements by type and sub-type.
     * This is a Many-to-One relationship because one category can have many advertisements.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_adv_category"))
    private Category category;

    /**
     * Optimistic-locking version column to prevent lost-update concurrency issues.
     * Incremented by Hibernate on every update; concurrent updates on the same version fail fast.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * JPA-required no-argument constructor.
     * Assigns a UUID, sets default status to PENDING, and initialises the version to 0L.
     */
    protected Adv() {
        this.id = UUID.randomUUID();
        this.status = AdvStatus.PENDING;
        this.version = 0L;
    }

    /**
     * Convenience constructor for creating a fully initialised advertisement.
     *
     * @param description free-text description written by the owner
     * @param advType     PRODUCT or SERVICE discriminator
     * @param user        the owner posting the advertisement
     * @param fullName    advertisement headline shown in listings
     * @param city        city where the item or service is located
     */
    protected Adv(String description, AdvType advType, User user, String fullName, City city) {
        this();
        this.description = description;
        this.advType = advType;
        this.user = user;
        this.fullName = fullName;
        this.city = city;
    }

    // ---------- Getters and Setters ----------
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

    public String getRejectionExplanation() { return rejectionExplanation; }
    public void setRejectionExplanation(String rejectionExplanation) { this.rejectionExplanation = rejectionExplanation; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Image> getImages() { return images; }
    public void setImages(List<Image> images) { this.images = images; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Long getVersion() { return version; }

    // ---------- Helper Methods ----------

    /**
     * Appends a key-value option attribute to this advertisement.
     * Also sets the back-reference on the option.
     *
     * @param option the attribute to add; must not be null
     */
    public void addOption(Option option) {
        if (option != null && !this.options.contains(option)) {
            this.options.add(option);
            option.setAdv(this);
        }
    }

    /**
     * Removes a key-value option attribute from this advertisement.
     * Also clears the back-reference on the option.
     *
     * @param option the attribute to remove
     */
    public void removeOption(Option option) {
        if (this.options.remove(option)) {
            option.setAdv(null);
        }
    }

    /**
     * Attaches a comment to this advertisement and sets the bidirectional back-reference.
     *
     * @param comment the comment to attach; must not be null
     */
    public void addComment(Comment comment) {
        if (comment != null && !this.comments.contains(comment)) {
            this.comments.add(comment);
            comment.setAdv(this);
        }
    }

    /**
     * Removes a comment from this advertisement and clears the back-reference.
     *
     * @param comment the comment to remove
     */
    public void removeComment(Comment comment) {
        if (this.comments.remove(comment)) {
            comment.setAdv(null);
        }
    }

    /**
     * Attaches an image to this advertisement.
     * Also sets the back-reference on the image.
     *
     * @param image the image to attach; must not be null
     */
    public void addImage(Image image) {
        if (image != null && !this.images.contains(image)) {
            this.images.add(image);
            image.setAdv(this);
        }
    }

    /**
     * Removes an image from this advertisement.
     * Also clears the back-reference on the image.
     *
     * @param image the image to remove
     */
    public void removeImage(Image image) {
        if (this.images.remove(image)) {
            image.setAdv(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adv other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "Adv{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                ", advType=" + advType +
                ", city=" + city +
                ", creationDate=" + creationDate +
                '}';
    }
}