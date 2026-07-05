package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents a single image attached to an advertisement.
 *
 * <p>Images are stored as filesystem or object-storage paths; the actual binary data
 * lives outside the database. Each {@link Image} record points to one file and is
 * owned exclusively by one {@link Adv}.</p>
 *
 * <p>Deleted automatically (via orphanRemoval) when the parent advertisement is removed.</p>
 */
@Entity
@Table(
        name = "images",
        indexes = {
                @Index(name = "idx_image_adv_id", columnList = "adv_id")
        }
)
public class Image {

    /**
     * Surrogate primary key for this image record.
     * Uses database-generated identity; no natural key exists.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Relative filesystem path or object-storage key for the image file.
     * Examples: "uploads/adverts/2024/abc123.jpg" or an S3 object key.
     * Must not be blank; uniqueness is not enforced at the DB level because
     * the same physical file could theoretically be referenced more than once.
     */
    @NotBlank(message = "Image path must not be blank")
    @Size(max = 500, message = "Image path must not exceed 500 characters")
    @Column(name = "path", nullable = false, length = 500)
    private String path;

    /**
     * Foreign key column managed by the parent {@link Adv} via @JoinColumn.
     * Declared here for index visibility; the value is controlled by the parent.
     */
    @Column(name = "adv_id", insertable = false, updatable = false)
    private Long advId;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     */
    public Image() {}

    /**
     * Convenience constructor for creating an image record with a known path.
     *
     * @param path relative filesystem path or object-storage key for the image file
     */
    public Image(String path) {
        this.path = path;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on surrogate identity key
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Safe toString.
     */
    @Override
    public String toString() {
        return "Image{id=" + id + ", path='" + path + "'}";
    }
}
