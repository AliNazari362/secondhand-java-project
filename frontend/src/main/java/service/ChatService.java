package service;

import model.request.ChatroomCreateRequest;
import model.request.MessageRequest;
import model.response.ChatroomDetailDto;
import model.response.ChatroomSummaryDto;
import model.response.MessageResponseDto;

import java.util.List;

public class ChatService {

    private final ApiClient api = ApiClient.getInstance();

    public ChatroomDetailDto startOrGetChat(ChatroomCreateRequest request) throws Exception {
        String response = api.post("/chats/start-chat", request);
        return api.fromJson(response, ChatroomDetailDto.class);
    }

    public List<ChatroomSummaryDto> getUserChatRooms() throws Exception {
        String response = api.get("/chats");
        ChatroomSummaryDto[] chats = api.fromJson(response, ChatroomSummaryDto[].class);
        return List.of(chats);
    }

    public ChatroomDetailDto getChatroomDetail(String chatId) throws Exception {
        String response = api.get("/chats/" + chatId);
        return api.fromJson(response, ChatroomDetailDto.class);
    }

    public MessageResponseDto sendMessage(String chatId, MessageRequest request) throws Exception {
        String response = api.post("/chats/" + chatId + "/send-message", request);
        return api.fromJson(response, MessageResponseDto.class);
    }

    public List<MessageResponseDto> getMessages(String chatId) throws Exception {
        String response = api.get("/chats/" + chatId + "/messages");
        MessageResponseDto[] messages = api.fromJson(response, MessageResponseDto[].class);
        return List.of(messages);
    }

}
