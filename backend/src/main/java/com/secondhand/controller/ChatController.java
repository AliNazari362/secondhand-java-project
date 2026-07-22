package com.secondhand.controller;


import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.chatroom.ChatroomSummaryResponse;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.dto.message.MessageResponse;
import com.secondhand.service.ChatService;
import com.secondhand.service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for chat and messaging operations.
 *
 * <p>Provides endpoints for starting chat rooms, listing the authenticated user's chat rooms,
 * retrieving chat room details, sending messages, and reading message history.
 * Base path: {@code /api/chats}</p>
 */
@RestController
@RequestMapping("api/chats")
public class ChatController {

    private final ChatService chatService;

    /**
     * Constructs a {@code ChatController} with the required service dependency.
     *
     * @param chatService the chat service used to handle chat room and messaging logic
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Starts a new chat room between the authenticated user and another party, or returns the
     * existing chat room if one already exists for the given advertisement.
     *
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body identifying the advertisement and participants
     * @return the {@link ChatroomDetailResponse} of the existing or newly created chat room
     */
    @PostMapping("start-chat")
    public ChatroomDetailResponse startOrGetChat(@RequestHeader("Authorization") String token, @Valid @RequestBody ChatroomCreateRequest request) {
        return chatService.startOrGetChat(request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves a summary list of all chat rooms the authenticated user is participating in.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @return a list of {@link ChatroomSummaryResponse} objects for the authenticated user's chat rooms
     */
    @GetMapping
    public List<ChatroomSummaryResponse> getUserChatRooms(@RequestHeader("Authorization") String token) {
        return chatService.getUserChatRooms(JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves the full details of a specific chat room, including its messages.
     * The authenticated user must be a participant of the chat room.
     *
     * @param chatId the UUID of the chat room to retrieve
     * @param token  the JWT bearer token from the {@code Authorization} request header
     * @return the {@link ChatroomDetailResponse} containing chat room and participant information
     */
    @GetMapping("{chatId}")
    public ChatroomDetailResponse getChatroomDetail(@PathVariable UUID chatId, @RequestHeader("Authorization") String token) {
        return chatService.getChatroomDetail(chatId, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Sends a new message to the specified chat room on behalf of the authenticated user.
     *
     * @param chatId  the UUID of the chat room to send the message to
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing the message content
     * @return the {@link MessageResponse} representing the persisted message
     */
    @PostMapping("{chatId}/send-message")
    public MessageResponse sendMessage(@PathVariable UUID chatId, @RequestHeader("Authorization") String token, @Valid @RequestBody MessageRequest request) {
        return chatService.sendMessage(chatId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves the full message history for a specific chat room.
     * The authenticated user must be a participant of the chat room.
     *
     * @param chatId the UUID of the chat room whose messages are to be retrieved
     * @param token  the JWT bearer token from the {@code Authorization} request header
     * @return a list of {@link MessageResponse} objects ordered chronologically
     */
    @GetMapping("{chatId}/messages")
    public List<MessageResponse> getMessages(@PathVariable UUID chatId, @RequestHeader("Authorization") String token) {
        return chatService.getMessages(chatId, JwtUtil.getUserIdFromToken(token));
    }
}
