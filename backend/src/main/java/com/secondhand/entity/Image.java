package com.secondhand.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents an image attached to an {@link Adv advertisement}.
 *
 * <p>Images provide visual context for a listing, helping buyers assess the item or
 * service before making contact. The actual image file is stored on a file system or
 * object-storage service; this entity only persists the relative path (or URL)
 * needed to retrieve it.</p>
 *
 * <p>This is the owning side of the bidirectional {@code Image ↔ Adv} relationship;
 * the foreign key {@code adv_id} lives in the {@code images} table.</p>
 */
@Entity
@Table(name = "images", indexes = @Index(name = "idx_image_adv_id", columnList = "adv_id"))
public class Image {

    /**
     * Surrogate primary key for this image record.
     * Uses database-generated identity since images do not require a business key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Relative file-system path or URL pointing to the stored image file.
     * Must not be blank and is limited to 500 characters.
     */
    @NotBlank(message = "مسیر تصویر نمی‌تواند خالی باشد")
    @Size(max = 500, message = "مسیر تصویر نباید از ۵۰۰ کاراکتر بیشتر باشد")
    @Column(name = "path", nullable = false, length = 500)
    private String path;

    /**
     * The advertisement this image is attached to.
     * This is the owning side of the {@code Image ↔ Adv} relationship.
     */
    // ✅ رابطه‌ی ManyToOne با Adv (سمت owning)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_image_adv"))
    private Adv adv;

    /**
     * JPA-required no-argument constructor.
     */
    public Image() {}

    /**
     * Convenience constructor for creating a fully initialised image.
     *
     * @param path the file-system path or URL of the image
     * @param adv  the advertisement this image belongs to
     */
    public Image(String path, Adv adv) {
        this.path = path;
        this.adv = adv;
    }

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Adv getAdv() { return adv; }
    public void setAdv(Adv adv) { this.adv = adv; }

    // ---------- equals & hashCode ----------
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

    @Override
    public String toString() {
        return "Image{id=" + id + ", path='" + path + "'}";
    }
}
