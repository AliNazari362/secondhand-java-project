package Controller;


import DTO.chatroom.ChatroomCreateRequest;
import DTO.chatroom.ChatroomDetailResponse;
import DTO.chatroom.ChatroomSummaryResponse;
import DTO.message.MessageRequest;
import DTO.message.MessageResponse;
import Service.ChatService;
import Service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("start-chat")
    public ChatroomDetailResponse startOrGetChat(@RequestHeader("Authorization") String token, @RequestBody ChatroomCreateRequest request) {
        return chatService.startOrGetChat(request, JwtUtil.getUserIdFromToken(token));
    }

    @GetMapping
    public List<ChatroomSummaryResponse> getUserChatRooms(@RequestHeader("Authorization") String token) {
        return chatService.getUserChatRooms(JwtUtil.getUserIdFromToken(token));
    }

    @GetMapping("{chatId}")
    public ChatroomDetailResponse getChatroomDetail(@PathVariable UUID chatId, @RequestHeader("Authorization") String token) {
        return chatService.getChatroomDetail(chatId, JwtUtil.getUserIdFromToken(token));
    }

    @PostMapping("{chatId}/send-message")
    public MessageResponse sendMessage(@PathVariable UUID chatId, @RequestHeader("Authorization") String token, @RequestBody MessageRequest request) {
        return chatService.sendMessage(chatId, request, JwtUtil.getUserIdFromToken(token));
    }

    @GetMapping("{chatId}/messages")
    public List<MessageResponse> getMessages(@PathVariable UUID chatId, @RequestHeader("Authorization") String token) {
        return chatService.getMessages(chatId, JwtUtil.getUserIdFromToken(token));
    }
}
