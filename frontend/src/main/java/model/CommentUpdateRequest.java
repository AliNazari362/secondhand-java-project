package model;

public class CommentUpdateRequest {
    private String text;
    private int rate;

    public CommentUpdateRequest() {}

    public CommentUpdateRequest(String text, int rate) {
        this.text = text;
        this.rate = rate;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }
}