package com.secondhand.controller;


import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.chatroom.ChatroomSummaryResponse;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.dto.message.MessageResponse;
import com.secondhand.service.ChatService;
import com.secondhand.service.JwtUtil;
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
