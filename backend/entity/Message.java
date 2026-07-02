import java.time.LocalDateTime;

public class Message {
    private String text;
    private User user;         // فرستنده پیام
    private LocalDateTime date;
    private boolean seenOrNot;

    public Message() {
        this.date = LocalDateTime.now();
        this.seenOrNot = false;
    }

    public Message(String text, User user) {
        this.text = text;
        this.user = user;
        this.date = LocalDateTime.now();
        this.seenOrNot = false;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public boolean isSeenOrNot() { return seenOrNot; }
    public void setSeenOrNot(boolean seenOrNot) { this.seenOrNot = seenOrNot; }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + (user != null ? user.getUsername() : null) +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", seen=" + seenOrNot +
                '}';
    }
}