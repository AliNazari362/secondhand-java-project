package model.response;

import java.util.UUID;

public class ChatroomSummaryDto {
    private UUID id;
    private UUID advId;
    private String advTitle;
    private int messageCount;
    private long unreadCount;

    public ChatroomSummaryDto() {}

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAdvId() { return advId; }
    public void setAdvId(UUID advId) { this.advId = advId; }

    public String getAdvTitle() { return advTitle; }
    public void setAdvTitle(String advTitle) { this.advTitle = advTitle; }

    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}