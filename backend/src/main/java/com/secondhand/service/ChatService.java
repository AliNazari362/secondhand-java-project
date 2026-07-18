package com.secondhand.service;

import com.secondhand.dto.chatroom.ChatroomSummaryResponse;
import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.dto.message.MessageResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.*;
import com.secondhand.repository.ChatroomRepository;
import com.secondhand.repository.MessageRepository;
import com.secondhand.exception.ChatroomNotFoundException;
import com.secondhand.exception.IllegalOwnershipException;
import com.secondhand.exception.NotParticipantException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final AdvService advService;
    private final UserService userService;

    public ChatService(ChatroomRepository chatroomRepository,
                       MessageRepository messageRepository,
                       AdvService advService,
                       UserService userService) {
        this.chatroomRepository = chatroomRepository;
        this.messageRepository = messageRepository;
        this.advService = advService;
        this.userService = userService;
    }

    public ChatroomDetailResponse startOrGetChat(ChatroomCreateRequest request, UUID userId) {
        Adv adv = advService.findAdvById(request.advId());

        if (adv.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("شما نمی توانید در آگهی خود، گفت و گویی آغاز کنید");
        }

        Chatroom existing = chatroomRepository
                .findByUserIdAndAdvId(userId, request.advId())
                .orElse(null);

        if (existing != null) {
            return toChatroomDetailResponse(existing);
        }

        User buyer = userService.findUserById(userId);
        User seller = adv.getUser();
        Chatroom newRoom = new Chatroom(adv);

        buyer.addRoom(newRoom);
        seller.addRoom(newRoom);

        Chatroom saved = chatroomRepository.save(newRoom);
        return toChatroomDetailResponse(saved);
    }

    public List<ChatroomSummaryResponse> getUserChatRooms(UUID userId) {
        List<Chatroom> rooms = chatroomRepository.findByParticipantId(userId);
        return rooms.stream()
                .map(this::toChatroomSummaryResponse)
                .collect(Collectors.toList());
    }

    public ChatroomDetailResponse getChatroomDetail(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        messageRepository.markAllAsSeen(chatroomId, userId);
        return toChatroomDetailResponse(room);
    }

    public MessageResponse sendMessage(UUID chatroomId, MessageRequest request, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        User sender = userService.findUserById(userId);

        Message msg = new Message(request.text(), sender, room);
        room.addMessage(msg);

        Message saved = messageRepository.save(msg);
        return toMessageResponse(saved);
    }

    public List<MessageResponse> getMessages(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        List<Message> messages = messageRepository.findByChatroomIdOrderByDateAsc(chatroomId);
        return messages.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    private Chatroom findChatroomById(UUID chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatroomNotFoundException("گفت و گو یافت نشد"));
    }

    private void checkParticipant(Chatroom room, UUID userId) {
        if (room.getUserId() != null && room.getUserId().equals(userId)) {
            return;
        }

        Adv adv = room.getAdv();
        if (adv != null && adv.getUser() != null && adv.getUser().getId().equals(userId)) {
            return;
        }

        throw new NotParticipantException("شما اجازه شرکت در این گفت و گو را ندارید");
    }

    private ChatroomSummaryResponse toChatroomSummaryResponse(Chatroom room) {
        return new ChatroomSummaryResponse(
                room.getId(),
                room.getAdv().getId(),
                room.getAdv().getFullName(),
                room.getMessages().size(),
                messageRepository.countByChatroomIdAndSeenFalse(room.getId())
        );
    }

    private ChatroomDetailResponse toChatroomDetailResponse(Chatroom room) {
        List<MessageResponse> messages = room.getMessages().stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());

        return new ChatroomDetailResponse(
                room.getId(),
                room.getAdv().getId(),
                room.getAdv().getFullName(),
                messages
        );
    }

    private MessageResponse toMessageResponse(Message msg) {
        User sender = msg.getSender();
        return new MessageResponse(
                msg.getId(),
                msg.getText(),
                new UserSummaryResponse(
                        sender.getId(),
                        sender.getFullName(),
                        sender.getEmail(),
                        sender.getUserType()
                ),
                msg.getDate(),
                msg.isSeen()
        );
    }
}