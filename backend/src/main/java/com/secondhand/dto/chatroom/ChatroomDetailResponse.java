package com.secondhand.dto.chatroom;

import com.secondhand.dto.message.MessageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Detailed response DTO for a single chatroom, including its full message history.
 *
 * <p>Returned by GET /api/chatrooms/{chatroomId}. Messages are ordered chronologically
 * (oldest first) so the client can render the conversation in natural reading order.
 * To prevent circular references, the associated advertisement is referenced by ID and
 * title only.</p>
 *
 * @param id       the unique identifier of this chatroom
 * @param advId    the unique identifier of the associated advertisement
 * @param advTitle the headline of the associated advertisement
 * @param messages the full, ordered list of messages in this chatroom
 */
public record ChatroomDetailResponse(

        UUID id,

        UUID advId,

        String advTitle,

        List<MessageResponse> messages

) {}