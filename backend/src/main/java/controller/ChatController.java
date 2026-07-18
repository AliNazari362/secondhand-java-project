package controller;

import dto.chatroom.ChatroomCreateRequest;
import dto.chatroom.ChatroomDetailResponse;
import dto.chatroom.ChatroomSummaryResponse;
import dto.message.MessageRequest;
import dto.message.MessageResponse;
import exception.IllegalTokenException;
import service.ChatService;
import service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatroomDetailResponse startChat(@RequestHeader("Authorization") String header,
                                            @Valid @RequestBody ChatroomCreateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return chatService.startOrGetChat(request, userId);
    }

    @GetMapping
    public List<ChatroomSummaryResponse> getUserChatrooms(@RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return chatService.getUserChatrooms(userId);
    }

    @GetMapping("/{chatroomId}")
    public ChatroomDetailResponse getChatroomDetail(@PathVariable UUID chatroomId,
                                                    @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return chatService.getChatroomDetail(chatroomId, userId);
    }

    @GetMapping("/{chatroomId}/messages")
    public List<MessageResponse> getMessages(@PathVariable UUID chatroomId,
                                             @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return chatService.getMessages(chatroomId, userId);
    }

    @PostMapping("/{chatroomId}/messages")
    public MessageResponse sendMessage(@PathVariable UUID chatroomId,
                                       @RequestHeader("Authorization") String header,
                                       @Valid @RequestBody MessageRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return chatService.sendMessage(chatroomId, request, userId);
    }

    private UUID extractUserIdFromToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalTokenException("توکن نامعتبر است");
        }
        String token = header.substring(7);
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("توکن نامعتبر یا منقضی شده است");
        }
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        return UUID.fromString(userIdStr);
    }
}