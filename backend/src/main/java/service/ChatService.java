package service;

import dto.chatroom.ChatroomSummaryResponse;
import dto.chatroom.ChatroomDetailResponse;
import dto.chatroom.ChatroomCreateRequest;
import dto.message.MessageRequest;
import dto.message.MessageResponse;
import dto.user.UserSummaryResponse;
import entity.*;
import Repository.ChatroomRepository;
import Repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
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
            throw new RuntimeException("You cannot chat about your own advertisement");
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

    public List<ChatroomSummaryResponse> getUserChatrooms(UUID userId) {
        List<Chatroom> rooms = chatroomRepository.findByParticipantId(userId);
        return rooms.stream()
                .map(this::toChatroomSummaryResponse)
                .collect(Collectors.toList());
    }

    public ChatroomDetailResponse getChatroomDetail(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);

        if (!isParticipant(room, userId)) {
            throw new RuntimeException("You are not a participant in this chatroom");
        }

        messageRepository.markAllAsSeen(chatroomId, userId);
        return toChatroomDetailResponse(room);
    }

    public MessageResponse sendMessage(UUID chatroomId, MessageRequest request, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);

        if (!isParticipant(room, userId)) {
            throw new RuntimeException("You are not a participant in this chatroom");
        }

        User sender = userService.findUserById(userId);

        Message msg = new Message(request.text(), sender, room);
        room.addMessage(msg);

        Message saved = messageRepository.save(msg);
        return toMessageResponse(saved);
    }

    public List<MessageResponse> getMessages(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);

        if (!isParticipant(room, userId)) {
            throw new RuntimeException("You are not a participant in this chatroom");
        }

        List<Message> messages = messageRepository.findByChatroomIdOrderByDateAsc(chatroomId);
        return messages.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    private Chatroom findChatroomById(UUID chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    }

    private boolean isParticipant(Chatroom room, UUID userId) {
        if (room.getUserId() != null && room.getUserId().equals(userId)) {
            return true;
        }
        Adv adv = room.getAdv();
        if (adv != null && adv.getUser() != null && adv.getUser().getId().equals(userId)) {
            return true;
        }
        return false;
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