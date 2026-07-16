package Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(
        name = "options",
        indexes = {
                @Index(name = "idx_option_adv_id", columnList = "adv_id"),
                @Index(name = "idx_option_opt_name", columnList = "opt_name")
        }
)
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "Option name must not be blank")
    @Size(max = 150, message = "Option name must not exceed 150 characters")
    @Column(name = "opt_name", nullable = false, length = 150)
    private String option;

    @NotBlank(message = "Option value must not be blank")
    @Size(max = 500, message = "Option value must not exceed 500 characters")
    @Column(name = "val_name", nullable = false, length = 500)
    private String value;

    // ✅ رابطه‌ی ManyToOne با Adv (سمت owning)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_option_adv"))
    private Adv adv;

    public Option() {}

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