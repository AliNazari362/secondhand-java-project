import java.time.LocalDateTime;

public class Comment {
    private String text;
    private int rate;          // بین ۱ تا ۵
    private User user;         // شخصی که نظر داده
    private LocalDateTime date;
    private Adv adv;           // آگهی مربوطه

    public Comment() {
        this.date = LocalDateTime.now();
    }

    public Comment(String text, int rate, User user, Adv adv) {
        this.text = text;
        this.rate = rate;
        this.user = user;
        this.adv = adv;
        this.date = LocalDateTime.now();
    }

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

    @Override
    public String toString() {
        return "Comment{" +
                "user=" + (user != null ? user.getUsername() : null) +
                ", rate=" + rate +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}