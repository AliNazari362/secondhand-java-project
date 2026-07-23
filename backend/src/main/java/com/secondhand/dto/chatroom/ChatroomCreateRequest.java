package com.secondhand.dto.chatroom;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for opening a new chatroom on a specific advertisement.
 *
 * <p>Submitted via POST /api/chatrooms. The initiating user is derived from the
 * security context by the service layer and is not included in this DTO.</p>
 *
 * @param advId the unique identifier of the advertisement to start a conversation about
 */
public record ChatroomCreateRequest(

        @NotNull(message = "شناسه آگهی نمی‌تواند خالی باشد")
        UUID advId

) {}