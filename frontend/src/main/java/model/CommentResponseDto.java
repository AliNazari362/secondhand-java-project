package model;

import java.time.LocalDateTime;

public class CommentResponseDto {
    private Long id;
    private String text;
    private int rate;
    private UserSummaryDto author;
    private LocalDateTime date;

    public CommentResponseDto() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }

    public UserSummaryDto getAuthor() { return author; }
    public void setAuthor(UserSummaryDto author) { this.author = author; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}