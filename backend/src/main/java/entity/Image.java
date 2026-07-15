package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "images", indexes = @Index(name = "idx_image_adv_id", columnList = "adv_id"))
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "Image path must not be blank")
    @Size(max = 500, message = "Image path must not exceed 500 characters")
    @Column(name = "path", nullable = false, length = 500)
    private String path;

    // ✅ رابطه‌ی ManyToOne با Adv (سمت owning)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_image_adv"))
    private Adv adv;

    public Image() {}

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