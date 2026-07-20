//package service;
//
//import model.request.ChatroomCreateRequest;
//import model.request.MessageRequest;
//import model.response.ChatroomDetailDto;
//import model.response.ChatroomSummaryDto;
//import model.response.MessageResponseDto;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//public class ChatService {
//
//    public ChatroomDetailDto startChat(ChatroomCreateRequest request) throws Exception {
//        String json = ApiClient.post("/chats/start-chat", request);
//        return ApiClient.fromJson(json, ChatroomDetailDto.class);
//    }
//
//    public List<ChatroomSummaryDto> getUserChatRooms() throws Exception {
//        String json = ApiClient.get("/chats");
//        return Arrays.asList(ApiClient.fromJson(json, ChatroomSummaryDto[].class));
//    }
//
//    public ChatroomDetailDto getChatroomDetail(UUID chatId) throws Exception {
//        String json = ApiClient.get("/chats/" + chatId);
//        return ApiClient.fromJson(json, ChatroomDetailDto.class);
//    }
//
//    public MessageResponseDto sendMessage(UUID chatId, MessageRequest request) throws Exception {
//        String json = ApiClient.post("/chats/" + chatId + "/send-message", request);
//        return ApiClient.fromJson(json, MessageResponseDto.class);
//    }
//
//    public List<MessageResponseDto> getMessages(UUID chatId) throws Exception {
//        String json = ApiClient.get("/chats/" + chatId + "/messages");
//        return Arrays.asList(ApiClient.fromJson(json, MessageResponseDto[].class));
//    }
//}