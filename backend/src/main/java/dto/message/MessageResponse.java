package dto.message;

import dto.user.UserSummaryResponse;
import dto.chatroom.ChatroomDetailResponse;

import java.time.LocalDateTime;

/**
 * Response DTO representing a single chat message within a chatroom.
 *
 * <p>Returned by GET /api/chatrooms/{chatroomId}/messages and embedded inside
 * {@link ChatroomDetailResponse}. The sender
 * is embedded as a lightweight {@link UserSummaryResponse} to prevent circular
 * references and avoid exposing sensitive user data.</p>
 *
 * @param id     the surrogate identifier of the message
 * @param text   the textual body of the message
 * @param sender the user who sent the message (summary, no sensitive data)
 * @param date   the timestamp when the message was sent
 * @param seen   whether the recipient has read the message
 */
public record MessageResponse(

        /** Surrogate database identifier for this message. */
        Long id,

        /** The textual body of the message. */
        String text,

        /** Lightweight representation of the message sender; excludes sensitive fields. */
        UserSummaryResponse sender,

        /** Timestamp of when the message was sent (persisted). */
        LocalDateTime date,

        /** {@code true} if the recipient has read this message; {@code false} otherwise. */
        boolean seen

) {}
