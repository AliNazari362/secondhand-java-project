package model.request;

public class MessageRequest {
    private String text;

    public MessageRequest() {}

    public MessageRequest(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}