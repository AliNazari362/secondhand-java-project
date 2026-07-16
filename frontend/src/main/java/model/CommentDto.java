package model;

public class CommentDto {
    private Long id;
    private String text;
    private int rate;
    private String userFullName;
    private String date;

    public CommentDto() {}

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}