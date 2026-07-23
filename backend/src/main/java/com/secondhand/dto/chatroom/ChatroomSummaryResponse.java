package com.secondhand.dto.chatroom;

import java.util.UUID;

/**
 * Lightweight response DTO representing a chatroom in list views.
 *
 * <p>Returned by GET /api/users/{userId}/chatrooms to give the user a quick overview
 * of all their active conversations without loading full message history.</p>
 *
 * @param id           the unique identifier of the chatroom
 * @param advId        the unique identifier of the associated advertisement
 * @param advTitle     the headline of the associated advertisement
 * @param messageCount total number of messages in this chatroom
 * @param unreadCount  number of messages not yet seen by the current user
 */
public record ChatroomSummaryResponse(

        UUID id,

        UUID advId,

        String advTitle,

        int messageCount,

        long unreadCount

) {}