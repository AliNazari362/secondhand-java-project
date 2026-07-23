package com.secondhand.dto.message;

import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;

/**
 * Response DTO representing a single chat message within a chatroom.
 *
 * <p>Returned by GET /api/chatrooms/{chatroomId}/messages and embedded inside
 * {@link ChatroomDetailResponse}. The sender is embedded as a lightweight
 * {@link UserSummaryResponse} to prevent circular references and avoid exposing
 * sensitive user data.</p>
 *
 * @param id     the surrogate identifier of the message
 * @param text   the textual body of the message
 * @param sender the user who sent the message (summary, no sensitive data)
 * @param date   the timestamp when the message was sent
 * @param seen   whether the recipient has read the message
 */
public record MessageResponse(

        Long id,

        String text,

        UserSummaryResponse sender,

        LocalDateTime date,

        boolean seen

) {}