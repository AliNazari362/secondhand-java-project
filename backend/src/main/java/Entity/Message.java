package Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "Message text must not be blank")
    @Size(max = 5000, message = "Message text must not exceed 5000 characters")
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    @NotNull(message = "Message sender must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(name = "seen", nullable = false)
    private boolean seen;

    // ✅ فقط این رابطه – هیچ فیلد جداگانه‌ای برای chatroom_id وجود ندارد
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chatroom_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_chatroom"))
    private Chatroom chatroom;

    public Message() {
        this.seen = false;
    }

    public Message(String text, User sender, Chatroom chatroom) {
        this();
        this.text = text;
        this.sender = sender;
        this.chatroom = chatroom;
    }

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
    public void markAsSeen() { this.seen = true; }

    public Chatroom getChatroom() { return chatroom; }
    public void setChatroom(Chatroom chatroom) { this.chatroom = chatroom; }

    // ---------- equals & hashCode ----------
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