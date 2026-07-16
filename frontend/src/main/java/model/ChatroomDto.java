package model;

import java.util.UUID;

public class ChatroomDto {
    private UUID id;
    private UUID advId;
    private String advTitle;
    private String lastMessage;
    private int unreadCount;

    public ChatroomDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAdvId() { return advId; }
    public void setAdvId(UUID advId) { this.advId = advId; }

    public String getAdvTitle() { return advTitle; }
    public void setAdvTitle(String advTitle) { this.advTitle = advTitle; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}