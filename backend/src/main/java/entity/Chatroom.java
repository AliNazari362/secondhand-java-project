package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "chatrooms",
        indexes = {
                @Index(name = "idx_chatroom_adv_id", columnList = "adv_id"),
                @Index(name = "idx_chatroom_user_id", columnList = "user_id")
        }
)
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Chatroom must be associated with an advertisement")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adv_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_chatroom_adv"))
    private Adv adv;

    // ✅ اصلاح: استفاده از mappedBy به‌جای @JoinColumn
    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date ASC")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    public Chatroom() {
        this.id = UUID.randomUUID();
    }

    public Chatroom(Adv adv) {
        this();
        this.adv = adv;
    }

    public UUID getId() { return id; }

    public Adv getAdv() { return adv; }
    public void setAdv(Adv adv) { this.adv = adv; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public UUID getUserId() {
        return userId;
    }

    // ✅ اصلاح: تنظیم رابطه‌ی دوطرفه
    public void addMessage(Message message) {
        if (message != null && !this.messages.contains(message)) {
            this.messages.add(message);
            message.setChatroom(this); // این خط را اضافه کنید
        }
    }

    public void removeMessage(Message message) {
        if (this.messages.remove(message)) {
            message.setChatroom(null);
        }
    }

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