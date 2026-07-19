package com.secondhand.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a conversation thread between a buyer and a seller about a specific advertisement.
 *
 * <p>A chatroom is created the first time a potential buyer initiates contact regarding
 * an {@link Adv advertisement}. All subsequent {@link Message messages} exchanged between
 * the two parties are stored within this chatroom, ordered chronologically.</p>
 *
 * <p>The owning user of the chatroom is stored via the {@code user_id} column (managed
 * externally by the {@link User#rooms} join column). The advertisement link is the
 * mandatory association that gives the conversation its context.</p>
 */
@Entity
@Table(
        name = "chatrooms",
        indexes = {
                @Index(name = "idx_chatroom_adv_id", columnList = "adv_id"),
                @Index(name = "idx_chatroom_user_id", columnList = "user_id")
        }
)
public class Chatroom {

    /**
     * Globally unique identifier for this chatroom.
     * Generated once at application level and never updated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The advertisement this conversation is about.
     * Every chatroom must be linked to exactly one advertisement; the association is mandatory.
     */
    @NotNull(message = "اتاق گفت‌وگو باید با یک آگهی مرتبط باشد")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_chatroom_adv"))
    private Adv adv;

    /**
     * Ordered list of messages exchanged in this chatroom, sorted ascending by date.
     * Cascade ALL ensures messages are persisted/removed together with the chatroom.
     */
    // ✅ اصلاح: استفاده از mappedBy به‌جای @JoinColumn
    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date ASC")
    private List<Message> messages = new ArrayList<>();

    /**
     * Read-only projection of the {@code user_id} foreign key column.
     * Managed by the {@link User#rooms} association; not directly updatable here.
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    /**
     * JPA-required no-argument constructor.
     * Assigns a UUID so the entity has an identity before it is persisted.
     */
    public Chatroom() {
        this.id = UUID.randomUUID();
    }

    /**
     * Convenience constructor for creating a chatroom linked to a specific advertisement.
     *
     * @param adv the advertisement this chatroom is about; must not be null
     */
    public Chatroom(Adv adv) {
        this();
        this.adv = adv;
    }

    /**
     * Returns the unique identifier of this chatroom.
     *
     * @return the UUID primary key
     */
    public UUID getId() { return id; }

    /**
     * Returns the advertisement associated with this chatroom.
     *
     * @return the linked advertisement
     */
    public Adv getAdv() { return adv; }

    /**
     * Sets the advertisement associated with this chatroom.
     *
     * @param adv the advertisement to associate; must not be null
     */
    public void setAdv(Adv adv) { this.adv = adv; }

    /**
     * Returns the list of messages in this chatroom, ordered by date ascending.
     *
     * @return mutable list of messages
     */
    public List<Message> getMessages() { return messages; }

    /**
     * Replaces the entire message list for this chatroom.
     *
     * @param messages the new list of messages
     */
    public void setMessages(List<Message> messages) { this.messages = messages; }

    /**
     * Returns the read-only projection of the buyer's user ID stored in the join column.
     *
     * @return the UUID of the user who owns this chatroom, or {@code null} if not yet persisted
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Adds a message to this chatroom and sets the bidirectional back-reference.
     * Guards against duplicate entries.
     *
     * @param message the message to add; must not be null
     */
    // ✅ اصلاح: تنظیم رابطه‌ی دوطرفه
    public void addMessage(Message message) {
        if (message != null && !this.messages.contains(message)) {
            this.messages.add(message);
            message.setChatroom(this); // این خط را اضافه کنید
        }
    }

    /**
     * Removes a message from this chatroom and clears the back-reference on the message.
     *
     * @param message the message to remove
     */
    public void removeMessage(Message message) {
        if (this.messages.remove(message)) {
            message.setChatroom(null);
        }
    }

    /**
     * Counts the number of messages in this chatroom that have not yet been seen by the recipient.
     *
     * @return the count of unread messages
     */
    public long countUnreadMessages() {
        return messages.stream().filter(m -> !m.isSeen()).count();
    }

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

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + id +
                '}';
    }
}
