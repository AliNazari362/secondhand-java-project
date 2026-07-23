package service;

import model.request.ChatroomCreateRequest;
import model.request.MessageRequest;
import model.response.ChatroomDetailDto;
import model.response.ChatroomSummaryDto;
import model.response.MessageResponseDto;

import java.util.List;

/**
 * Service class for chat-related API operations.
 * Provides methods for managing chat rooms and messages between users.
 */
public class ChatService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Starts a new chat or retrieves an existing chat room for an advertisement.
     *
     * @param request the chat room creation request containing advertisement ID and recipient
     * @return the chat room detail DTO
     * @throws Exception if the API request fails
     */
    public static ChatroomDetailDto startOrGetChat(ChatroomCreateRequest request) throws Exception {
        String response = api.post("/chats/start-chat", request);
        return api.fromJson(response, ChatroomDetailDto.class);
    }

    /**
     * Retrieves all chat rooms for the current user.
     *
     * @return a list of chat room summaries with unread counts
     * @throws Exception if the API request fails
     */
    public static List<ChatroomSummaryDto> getUserChatRooms() throws Exception {
        String response = api.get("/chats");
        ChatroomSummaryDto[] chats = api.fromJson(response, ChatroomSummaryDto[].class);
        return List.of(chats);
    }

    /**
     * Retrieves detailed information for a specific chat room.
     *
     * @param chatId the chat room ID
     * @return the chat room detail DTO with participant information
     * @throws Exception if the API request fails
     */
    public static ChatroomDetailDto getChatroomDetail(String chatId) throws Exception {
        String response = api.get("/chats/" + chatId);
        return api.fromJson(response, ChatroomDetailDto.class);
    }

    /**
     * Sends a message in a chat room.
     *
     * @param chatId  the chat room ID
     * @param request the message request containing the message text
     * @return the created message response DTO
     * @throws Exception if the API request fails
     */
    public static MessageResponseDto sendMessage(String chatId, MessageRequest request) throws Exception {
        String response = api.post("/chats/" + chatId + "/send-message", request);
        return api.fromJson(response, MessageResponseDto.class);
    }

    /**
     * Retrieves all messages for a specific chat room.
     *
     * @param chatId the chat room ID
     * @return a list of messages in the chat room
     * @throws Exception if the API request fails
     */
    public static List<MessageResponseDto> getMessages(String chatId) throws Exception {
        String response = api.get("/chats/" + chatId + "/messages");
        MessageResponseDto[] messages = api.fromJson(response, MessageResponseDto[].class);
        return List.of(messages);
    }

}