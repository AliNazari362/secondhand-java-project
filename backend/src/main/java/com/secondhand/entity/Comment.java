package com.secondhand.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user-submitted comment and rating on a specific advertisement.
 *
 * <p>Comments form the social-proof layer of the platform: buyers can leave feedback
 * after interacting with a seller, helping future buyers assess the seller's reliability
 * and the quality of the advertised item or com.secondhand.service.</p>
 *
 * <p>Each comment carries a 1–5 star rating that contributes to the advertisement's
 * aggregate score.</p>
 */
@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comment_adv_id",  columnList = "adv_id"),
                @Index(name = "idx_comment_user_id", columnList = "user_id"),
                @Index(name = "idx_comment_date",    columnList = "date")
        }
)
public class Comment {

    /**
     * Surrogate primary key for this comment.
     * Uses database-generated identity since comments do not require a business key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * The textual body of the comment written by the user.
     * Must not be blank; limited to 2000 characters to prevent abuse.
     */
    @NotBlank(message = "متن نظر نمی‌تواند خالی باشد")
        @Size(max = 2000, message = "متن نظر نباید از ۲۰۰۰ کاراکتر بیشتر باشد")
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * Numeric star rating given by the commenter, on a scale from 1 (worst) to 5 (best).
     * Contributes to the advertisement's overall average rating score.
     */
    @Min(value = 1, message = "امتیاز حداقل باید ۱ باشد")
        @Max(value = 5, message = "امتیاز حداکثر می‌تواند ۵ باشد")
    @Column(name = "rate", nullable = false)
    private int rate;

    /**
     * The user who wrote this comment.
     * Every comment must have an author; the association is mandatory.
     */
    @NotNull(message = "نویسنده نظر نمی‌تواند خالی باشد")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_comment_user"))
    private User user;

    /**
     * Timestamp of when this comment was first persisted.
     * Set automatically by Hibernate; immutable after creation.
     */
    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    /**
     * The advertisement that this comment belongs to.
     * Every comment targets exactly one advertisement.
     */
    @NotNull(message = "آگهی مرتبط با نظر نمی‌تواند خالی باشد")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_comment_adv"))
    private Adv adv;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     */
    public Comment() {}

    /**
     * Convenience constructor for creating a fully initialised comment.
     *
     * @param text the body of the comment
     * @param rate a 1–5 star rating
     * @param user the author of the comment
     * @param adv  the advertisement being commented on
     */
    public Comment(String text, int rate, User user, Adv adv) {
        this.text = text;
        this.rate = rate;
        this.user = user;
        this.adv = adv;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public int getRate() { return rate; }

    public void setRate(int rate) { this.rate = rate; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public Adv getAdv() { return adv; }

    public void setAdv(Adv adv) { this.adv = adv; }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on surrogate identity key
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment other)) return false;
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
        return "Comment{" +
                "id=" + id +
                ", rate=" + rate +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
