package model.response;

import java.util.List;
import java.util.UUID;

public class ChatroomDetailDto {
    private UUID id;
    private UUID advId;
    private String advTitle;
    private List<MessageResponseDto> messages;

    public ChatroomDetailDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAdvId() { return advId; }
    public void setAdvId(UUID advId) { this.advId = advId; }

    public String getAdvTitle() { return advTitle; }
    public void setAdvTitle(String advTitle) { this.advTitle = advTitle; }

    public List<MessageResponseDto> getMessages() { return messages; }
    public void setMessages(List<MessageResponseDto> messages) { this.messages = messages; }
}