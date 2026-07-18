package model.response;

import java.time.LocalDateTime;

public class MessageResponseDto {
    private Long id;
    private String text;
    private UserSummaryDto sender;
    private LocalDateTime date;
    private boolean seen;

    public MessageResponseDto() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public UserSummaryDto getSender() { return sender; }
    public void setSender(UserSummaryDto sender) { this.sender = sender; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}