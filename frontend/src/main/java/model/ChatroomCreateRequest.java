package model;

import java.util.UUID;

public class ChatroomCreateRequest {
    private UUID advId;

    public ChatroomCreateRequest() {}

    public ChatroomCreateRequest(UUID advId) {
        this.advId = advId;
    }

    public UUID getAdvId() { return advId; }
    public void setAdvId(UUID advId) { this.advId = advId; }
}