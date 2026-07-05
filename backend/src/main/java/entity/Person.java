package entity;

import entity.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base entity representing any authenticated principal in the system.
 *
 * <p>Uses a JOINED inheritance strategy so that concrete subtypes ({@link User})
 * are stored in their own tables while sharing the common columns defined here.</p>
 *
 * <p>The primary key is a UUID generated at the application level to avoid
 * database-specific auto-increment collisions across distributed deployments.</p>
 */
@Entity
@Table(
        name = "persons",
        indexes = {
                @Index(name = "idx_person_email",        columnList = "email",        unique = true),
                @Index(name = "idx_person_phone_number", columnList = "phone_number", unique = true)
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    /**
     * Globally unique identifier for this person.
     * Generated once at creation and never updated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * BCrypt-hashed password used for authentication.
     * Never stored or transmitted in plain text.
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Email address used as the unique login credential.
     * Must be unique across all persons in the system.
     */
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    /**
     * Mobile phone number of the person, stored in E.164-compatible format.
     * Optional but unique when provided; used for contact and verification.
     */
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain 7 to 15 digits, optionally prefixed with +"
    )
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    /**
     * Role of this person in the platform (USER or ADMIN).
     * Controls access to administrative features.
     */
    @NotNull(message = "User type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 10)
    private UserType userType;

    /**
     * JPA-required no-argument constructor.
     * Initialises the UUID so the entity has an identity before it is persisted.
     */
    protected Person() {
        this.id = UUID.randomUUID();
    }

    /**
     * Convenience constructor for creating a fully initialised person.
     *
     * @param password    BCrypt-hashed password
     * @param email       unique email address
     * @param phoneNumber optional phone number in E.164 format
     * @param userType    role of this person
     */
    protected Person(String password, String email, String phoneNumber, UserType userType) {
        this();
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserType getUserType() { return userType; }

    public void setUserType(UserType userType) { this.userType = userType; }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on natural business key (email)
    // Using the database-assigned id is dangerous before first flush.
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person other)) return false;
        return email != null && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        // Constant hash until email is set; safe for Hibernate proxies.
        return Objects.hashCode(email);
    }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userType=" + userType +
                '}';
    }
}
