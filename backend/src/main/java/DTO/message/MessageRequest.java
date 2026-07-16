package DTO.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for sending a new message in a chatroom.
 *
 * <p>Submitted via POST /api/chatrooms/{chatroomId}/messages. The sender identity
 * is derived from the security context by the service layer.</p>
 *
 * @param text the textual body of the message to send
 */
public record MessageRequest(

        /** The body of the message; must not be blank and must not exceed 5000 characters. */
        @NotBlank(message = "Message text must not be blank")
        @Size(max = 5000, message = "Message text must not exceed 5000 characters")
        String text

) {}
