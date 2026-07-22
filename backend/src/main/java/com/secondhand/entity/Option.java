package com.secondhand.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Represents an arbitrary key-value attribute attached to an {@link Adv advertisement}.
 *
 * <p>Options allow advertisement owners to specify structured metadata that does not fit
 * into the fixed columns of the advertisement tables. Examples include "Color=Black",
 * "RAM=8GB", or "Engine Displacement=1600cc".</p>
 *
 * <p>This is the owning side of the bidirectional {@code Option ↔ Adv} relationship;
 * the foreign key {@code adv_id} lives in the {@code options} table.</p>
 */
@Entity
@Table(
        name = "options",
        indexes = {
                @Index(name = "idx_option_adv_id", columnList = "adv_id"),
                @Index(name = "idx_option_opt_name", columnList = "opt_name")
        }
)
public class Option {

    /**
     * Surrogate primary key for this option entry.
     * Uses database-generated identity since options do not require a business key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Name (key) of the attribute, such as "Color", "RAM", or "Engine Size".
     * Must not be blank and is limited to 150 characters.
     */
    @NotBlank(message = "نام گزینه نمی‌تواند خالی باشد")
    @Size(max = 150, message = "نام گزینه نباید از ۱۵۰ کاراکتر بیشتر باشد")
    @Column(name = "opt_name", nullable = false, length = 150)
    private String option;

    /**
     * Value of the attribute, such as "Black", "8GB", or "1600cc".
     * Must not be blank and is limited to 500 characters.
     */
    @NotBlank(message = "مقدار گزینه نمی‌تواند خالی باشد")
    @Size(max = 500, message = "مقدار گزینه نباید از ۵۰۰ کاراکتر بیشتر باشد")
    @Column(name = "val_name", nullable = false, length = 500)
    private String value;

    /**
     * The advertisement this option belongs to.
     * This is the owning side of the {@code Option ↔ Adv} relationship.
     */
    // ✅ رابطه‌ی ManyToOne با Adv (سمت owning)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_option_adv"))
    private Adv adv;

    /**
     * JPA-required no-argument constructor.
     */
    public Option() {}

    /**
     * Convenience constructor for creating a fully initialised option.
     *
     * @param option the attribute name (key)
     * @param value  the attribute value
     * @param adv    the advertisement this option belongs to
     */
    public Option(String option, String value, Adv adv) {
        this.option = option;
        this.value = value;
        this.adv = adv;
    }

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Adv getAdv() { return adv; }
    public void setAdv(Adv adv) { this.adv = adv; }

    // ---------- equals & hashCode ----------
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

    @Override
    public String toString() {
        return "Option{id=" + id + ", " + option + " : " + value + '}';
    }
}
