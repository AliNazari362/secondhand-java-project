package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single chat message sent within a {@link Chatroom}.
 *
 * <p>Messages are the fundamental unit of communication between users negotiating
 * over an advertisement. Each message has exactly one sender and a read/unread state
 * that allows the recipient to see which messages are new.</p>
 *
 * <p>Messages are ordered chronologically (ascending) within their parent chatroom.</p>
 */
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_message_chatroom_id", columnList = "chatroom_id"),
                @Index(name = "idx_message_sender_id",   columnList = "sender_id"),
                @Index(name = "idx_message_date",        columnList = "date"),
                @Index(name = "idx_message_seen",        columnList = "seen")
        }
)
public class Message {

    /**
     * Surrogate primary key for this message.
     * Uses database-generated identity; messages have no natural business key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Textual content of the message written by the sender.
     * Must not be blank; limited to 5000 characters to prevent abuse.
     */
    @NotBlank(message = "Message text must not be blank")
    @Size(max = 5000, message = "Message text must not exceed 5000 characters")
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * The user who sent this message.
     * Every message must have a sender; the association is mandatory.
     */
    @NotNull(message = "Message sender must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    /**
     * Timestamp of when this message was persisted (i.e., sent).
     * Set automatically by Hibernate; immutable after creation.
     */
    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    /**
     * Read/unread flag for this message.
     * {@code false} means the message has not yet been seen by the recipient.
     * {@code true} means the recipient has opened and read the message.
     * Defaults to {@code false} at creation.
     */
    @Column(name = "seen", nullable = false)
    private boolean seen;

    /**
     * Foreign key column managed by the parent {@link Chatroom} via @JoinColumn.
     * Declared here for index visibility; the value is controlled by the parent.
     */
    @Column(name = "chatroom_id", insertable = false, updatable = false)
    private Long chatroomId;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     * Initialises {@code seen} to {@code false}.
     */
    public Message() {
        this.seen = false;
    }

    /**
     * Convenience constructor for creating a new outgoing message.
     *
     * @param text   the message body
     * @param sender the user sending the message
     */
    public Message(String text, User sender) {
        this();
        this.text = text;
        this.sender = sender;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public User getSender() { return sender; }

    public void setSender(User sender) { this.sender = sender; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    /**
     * Returns {@code true} if this message has been read by the recipient.
     */
    public boolean isSeen() { return seen; }

    public void setSeen(boolean seen) { this.seen = seen; }

    /**
     * Marks this message as read by the recipient.
     * Convenience method used by the messaging service layer.
     */
    public void markAsSeen() { this.seen = true; }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on surrogate identity key
    // -------------------------------------------------------------------------

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

    /**
     * Safe toString that never accesses lazy associations.
     */
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
