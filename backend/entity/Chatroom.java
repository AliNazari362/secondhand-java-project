import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    private Adv adv;
    private List<Message> messages;

    public Chatroom() {
        this.messages = new ArrayList<>();
    }

    public Chatroom(Adv adv) {
        this();
        this.adv = adv;
    }

    public Adv getAdv() { return adv; }
    public void setAdv(Adv adv) { this.adv = adv; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public void addMessage(Message message) { this.messages.add(message); }

    @Override
    public String toString() {
        return "Chatroom{" +
                "adv=" + (adv != null ? adv.getFullName() : null) +
                ", messagesCount=" + messages.size() +
                '}';
    }
}