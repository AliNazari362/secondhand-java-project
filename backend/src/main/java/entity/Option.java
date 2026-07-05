package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents a single key-value attribute that further describes an advertisement.
 *
 * <p>Options allow advertisement owners to supply structured, searchable metadata
 * beyond what the fixed entity fields provide. Examples:</p>
 * <ul>
 *   <li>RAM = 8 GB</li>
 *   <li>Color = Midnight Black</li>
 *   <li>Engine Displacement = 1600 cc</li>
 *   <li>Screen Size = 6.5 inch</li>
 * </ul>
 *
 * <p>Each {@link Option} is owned exclusively by one {@link Adv} and is deleted
 * automatically (via orphanRemoval) when the parent advertisement is removed.</p>
 */
@Entity
@Table(
        name = "options",
        indexes = {
                @Index(name = "idx_option_adv_id",   columnList = "adv_id"),
                @Index(name = "idx_option_opt_name", columnList = "opt_name")
        }
)
public class Option {

    /**
     * Surrogate primary key for this option record.
     * Uses database-generated identity; no natural key exists for a key-value pair.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * The name of the attribute key (e.g., "RAM", "Color", "Engine Displacement").
     * Describes which property of the item this option records.
     */
    @NotBlank(message = "Option name must not be blank")
    @Size(max = 150, message = "Option name must not exceed 150 characters")
    @Column(name = "opt_name", nullable = false, length = 150)
    private String option;

    /**
     * The value assigned to the attribute key (e.g., "8 GB", "Midnight Black", "1600 cc").
     * Provides the specific detail for the property named by {@link #option}.
     */
    @NotBlank(message = "Option value must not be blank")
    @Size(max = 500, message = "Option value must not exceed 500 characters")
    @Column(name = "val_name", nullable = false, length = 500)
    private String value;

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
    public Option() {}

    /**
     * Convenience constructor for creating a key-value option pair.
     *
     * @param option the name of the attribute key
     * @param value  the value for the attribute
     */
    public Option(String option, String value) {
        this.option = option;
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getOption() { return option; }

    public void setOption(String option) { this.option = option; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on surrogate identity key
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option other)) return false;
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
        return "Option{id=" + id + ", " + option + " : " + value + '}';
    }
}
