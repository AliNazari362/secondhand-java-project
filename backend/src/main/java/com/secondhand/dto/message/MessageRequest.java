package com.secondhand.dto.message;

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

        @NotBlank(message = "متن پیام نمی‌تواند خالی باشد")
        @Size(max = 5000, message = "متن پیام نباید از ۵۰۰۰ کاراکتر بیشتر باشد")
        String text

) {}