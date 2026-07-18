package com.secondhand.entity;

import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered user of the secondhand marketplace platform.
 *
 * <p>Extends {@link Person} via a JOINED inheritance strategy, meaning the
 * user-specific columns live in the {@code users} table while the shared
 * columns (email, password, etc.) stay in the {@code persons} table.</p>
 *
 * <p>A user can:</p>
 * <ul>
 *   <li>Post advertisements (products or services).</li>
 *   <li>Save advertisements to their favourites list.</li>
 *   <li>Participate in chatrooms associated with advertisements.</li>
 *   <li>Leave comments on advertisements.</li>
 * </ul>
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_status", columnList = "user_status")
        }
)
@PrimaryKeyJoinColumn(name = "person_id")
public class User extends Person {

    /**
     * Current account status (ACTIVE or BANNED).
     * A banned user cannot log in or interact with the platform.
     */
    @NotNull(message = "User status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 10)
    private UserStatus userStatus;

    /**
     * Display name of the user shown on advertisements and comments.
     * Not unique; used for human-readable identification only.
     */
    @NotBlank(message = "Full name must not be blank")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Advertisements saved by this user as favourites.
     * Allows users to bookmark listings they are interested in.
     * This is the owning side of the many-to-many relationship.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "adv_id", nullable = false)
    )
    private List<Adv> favorites = new ArrayList<>();

    /**
     * Chatrooms in which this user is a participant.
     * Each chatroom is linked to a specific advertisement negotiation thread.
     * This side owns the foreign key via @JoinColumn.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private List<Chatroom> rooms = new ArrayList<>();

    /**
     * All advertisements posted by this user.
     * Mapped by the {@code user} field on {@link Adv}.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Adv> userAdv = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     */
    public User() {
        super();
    }

    /**
     * Convenience constructor for creating a fully initialised user.
     *
     * @param password    BCrypt-hashed password
     * @param email       unique email address
     * @param phoneNumber optional phone number
     * @param userType    role (USER or ADMIN)
     * @param userStatus  account status (ACTIVE or BANNED)
     * @param fullName    display name
     */
    public User(String password, String email, String phoneNumber,
                UserType userType, UserStatus userStatus, String fullName) {
        super(password, email, phoneNumber, userType);
        this.userStatus = userStatus;
        this.fullName = fullName;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public UserStatus getUserStatus() { return userStatus; }

    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public List<Adv> getFavorites() { return favorites; }

    public void setFavorites(List<Adv> favorites) { this.favorites = favorites; }

    public List<Chatroom> getRooms() { return rooms; }

    public void setRooms(List<Chatroom> rooms) { this.rooms = rooms; }

    public List<Adv> getUserAdv() { return userAdv; }

    public void setUserAdv(List<Adv> userAdv) { this.userAdv = userAdv; }

    // -------------------------------------------------------------------------
    // Helper methods — always synchronise both sides of bidirectional relationships
    // -------------------------------------------------------------------------

    /**
     * Adds an advertisement to the user's favourites list.
     * Guards against duplicate entries.
     *
     * @param adv the advertisement to bookmark; must not be null
     */
    public void addFavorite(Adv adv) {
        if (adv != null && !this.favorites.contains(adv)) {
            this.favorites.add(adv);
        }
    }

    /**
     * Removes an advertisement from the user's favourites list.
     *
     * @param adv the advertisement to remove
     */
    public void removeFavorite(Adv adv) {
        this.favorites.remove(adv);
    }

    /**
     * Associates a chatroom with this user.
     * Guards against duplicate entries.
     *
     * @param room the chatroom to add; must not be null
     */
    public void addRoom(Chatroom room) {
        if (room != null && !this.rooms.contains(room)) {
            this.rooms.add(room);
        }
    }

    /**
     * Removes a chatroom from this user's room list.
     *
     * @param room the chatroom to remove
     */
    public void removeRoom(Chatroom room) {
        this.rooms.remove(room);
    }

    /**
     * Records an advertisement as belonging to this user.
     * Also sets the back-reference on the advertisement.
     *
     * @param adv the advertisement to register; must not be null
     */
    public void addUserAdv(Adv adv) {
        if (adv != null && !this.userAdv.contains(adv)) {
            this.userAdv.add(adv);
            adv.setUser(this);
        }
    }

    /**
     * Removes an advertisement from this user's posting list.
     * Also clears the back-reference on the advertisement.
     *
     * @param adv the advertisement to remove
     */
    public void removeUserAdv(Adv adv) {
        if (this.userAdv.remove(adv)) {
            adv.setUser(null);
        }
    }

    /**
     * Convenience accessor used in log messages and toString outputs.
     * Returns the email address as the unique textual identifier.
     */
    public String getUsername() {
        return getEmail();
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — inherited from Person (based on email)
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Safe toString that never accesses lazy associations.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", fullName='" + fullName + '\'' +
                ", email='" + getEmail() + '\'' +
                ", userStatus=" + userStatus +
                ", userType=" + getUserType() +
                '}';
    }
}
