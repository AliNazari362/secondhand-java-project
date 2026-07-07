package Service;

import dto.chatroom.ChatroomSummaryResponse;
import dto.chatroom.ChatroomDetailResponse;
import dto.chatroom.ChatroomCreateRequest;
import dto.message.MessageRequest;
import dto.message.MessageResponse;
import dto.user.UserSummaryResponse;
import entity.*;
import Repository.ChatroomRepository;
import Repository.MessageRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // ---------- شروع یا دریافت چت ----------
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
        User seller = adv.getUser(); // 🔥 فروشنده (صاحب آگهی)
        Chatroom newRoom = new Chatroom(adv);

        // هر دو طرف به rooms اضافه می‌شوند
        buyer.addRoom(newRoom);
        seller.addRoom(newRoom); // 🔥 این خط اضافه شد

        Chatroom saved = chatroomRepository.save(newRoom);
        return toChatroomDetailResponse(saved);
    }

    // ---------- دریافت لیست چت‌های کاربر (هم خریدار و هم فروشنده) ----------
    public List<ChatroomSummaryResponse> getUserChatrooms(UUID userId) {
        List<Chatroom> rooms = chatroomRepository.findByParticipantId(userId);
        return rooms.stream()
                .map(this::toChatroomSummaryResponse)
                .collect(Collectors.toList());
    }

    // ---------- دریافت جزئیات چت ----------
    public ChatroomDetailResponse getChatroomDetail(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);

        if (!isParticipant(room, userId)) {
            throw new RuntimeException("You are not a participant in this chatroom");
        }

        messageRepository.markAllAsSeen(chatroomId, userId);
        return toChatroomDetailResponse(room);
    }

    // ---------- ارسال پیام ----------
    public MessageResponse sendMessage(UUID chatroomId, MessageRequest request, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);

        if (!isParticipant(room, userId)) {
            throw new RuntimeException("You are not a participant in this chatroom");
        }

        User sender = userService.findUserById(userId);

        Message msg = new Message(request.text(), sender);
        room.addMessage(msg);

        Message saved = messageRepository.save(msg);
        return toMessageResponse(saved);
    }

    // ---------- دریافت پیام‌ها ----------
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

    // ---------- متدهای کمکی ----------
    private Chatroom findChatroomById(UUID chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    }

    private boolean isParticipant(Chatroom room, UUID userId) {
        // چک کردن خریدار
        if (room.getUserId() != null && room.getUserId().equals(userId)) {
            return true;
        }
        // چک کردن فروشنده
        Adv adv = room.getAdv();
        if (adv != null && adv.getUser() != null && adv.getUser().getId().equals(userId)) {
            return true;
        }
        return false;
    }

    // ---------- Mapperها ----------
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