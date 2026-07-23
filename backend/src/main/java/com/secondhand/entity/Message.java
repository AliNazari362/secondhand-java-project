package com.secondhand.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single message sent within a {@link Chatroom}.
 *
 * <p>Messages form the conversational history between a buyer and a seller.
 * Each message records its text, the sender, the timestamp of creation, and
 * whether it has been seen by the recipient.</p>
 */
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_message_chatroom_id", columnList = "chatroom_id"),
                @Index(name = "idx_message_sender_id", columnList = "sender_id"),
                @Index(name = "idx_message_date", columnList = "date"),
                @Index(name = "idx_message_seen", columnList = "seen")
        }
)
public class Message {

    /**
     * Surrogate primary key for this message.
     * Uses database-generated identity since messages do not require a business key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * The textual content of the message written by the sender.
     * Must not be blank and is limited to 5000 characters.
     */
    @NotBlank(message = "متن پیام نمی‌تواند خالی باشد")
    @Size(max = 5000, message = "متن پیام نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * The user who sent this message.
     * Every message must have a sender; the association is mandatory.
     */
    @NotNull(message = "فرستنده پیام نمی‌تواند خالی باشد")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    /**
     * Timestamp of when this message was created.
     * Set automatically by Hibernate; immutable after creation.
     */
    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    /**
     * Flag indicating whether the recipient has read this message.
     * Defaults to {@code false} at creation; set to {@code true} via {@link #markAsSeen()}.
     */
    @Column(name = "seen", nullable = false)
    private boolean seen;

    /**
     * The chatroom this message belongs to.
     * This is the owning side of the {@code Message ↔ Chatroom} relationship;
     * the foreign key {@code chatroom_id} lives in the {@code messages} table.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chatroom_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_chatroom"))
    private Chatroom chatroom;

    /**
     * JPA-required no-argument constructor.
     * Initialises {@code seen} to {@code false}.
     */
    public Message() {
        this.seen = false;
    }

    /**
     * Convenience constructor for creating a fully initialised message.
     *
     * @param text     the textual content of the message
     * @param sender   the user who sends the message
     * @param chatroom the chatroom this message belongs to
     */
    public Message(String text, User sender, Chatroom chatroom) {
        this();
        this.text = text;
        this.sender = sender;
        this.chatroom = chatroom;
    }

    // ---------- Getters and Setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * Marks this message as seen by the recipient.
     */
    public void markAsSeen() {
        this.seen = true;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", seen=" + seen +
                '}';
    }
}