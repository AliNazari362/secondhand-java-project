package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a private messaging thread between a user and the owner of an advertisement.
 *
 * <p>A chatroom is always scoped to a specific advertisement: every negotiation or enquiry
 * about a listing takes place within its own chatroom. This allows both parties to review
 * the full conversation history in context.</p>
 *
 * <p>Chatrooms are owned by the enquiring {@link User} (stored via @JoinColumn on the
 * {@code users} side) and hold an ordered list of {@link Message} records.</p>
 */
@Entity
@Table(
        name = "chatrooms",
        indexes = {
                @Index(name = "idx_chatroom_adv_id",  columnList = "adv_id"),
                @Index(name = "idx_chatroom_user_id", columnList = "user_id")
        }
)
public class Chatroom {

    /**
     * Globally unique identifier for this chatroom.
     * Generated once at application level and never changed.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The advertisement this chatroom is associated with.
     * Every chatroom must reference exactly one advertisement; mandatory association.
     */
    @NotNull(message = "Chatroom must be associated with an advertisement")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_chatroom_adv"))
    private Adv adv;

    /**
     * Chronologically ordered list of messages exchanged in this chatroom.
     * New messages are appended; ordering is ascending by send date.
     * Deleted automatically (via orphanRemoval) when this chatroom is deleted.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    @OrderBy("date ASC")
    private List<Message> messages = new ArrayList<>();

    /**
     * Foreign key column managed by the parent {@link User} via @JoinColumn.
     * Declared here for index visibility; the value is controlled by the parent.
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA-required no-argument constructor.
     * Assigns a UUID so the entity has an identity before it is persisted.
     */
    public Chatroom() {
        this.id = UUID.randomUUID();
    }

    /**
     * Convenience constructor for creating a chatroom linked to an advertisement.
     *
     * @param adv the advertisement this chatroom belongs to; must not be null
     */
    public Chatroom(Adv adv) {
        this();
        this.adv = adv;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public Adv getAdv() { return adv; }

    public void setAdv(Adv adv) { this.adv = adv; }

    public List<Message> getMessages() { return messages; }

    public void setMessages(List<Message> messages) { this.messages = messages; }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    /**
     * Appends a message to this chatroom's conversation thread.
     *
     * @param message the message to add; must not be null
     */
    public void addMessage(Message message) {
        if (message != null && !this.messages.contains(message)) {
            this.messages.add(message);
        }
    }

    /**
     * Removes a message from this chatroom's conversation thread.
     *
     * @param message the message to remove
     */
    public void removeMessage(Message message) {
        this.messages.remove(message);
    }

    /**
     * Returns the number of messages with {@code seen == false} in this chatroom.
     * Useful for displaying unread-message badges without triggering additional queries
     * when the messages collection is already initialised.
     *
     * @return count of unread messages
     */
    public long countUnreadMessages() {
        return messages.stream().filter(m -> !m.isSeen()).count();
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — based on surrogate UUID key
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chatroom other)) return false;
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
        return "Chatroom{" +
                "id=" + id +
                '}';
    }
}
