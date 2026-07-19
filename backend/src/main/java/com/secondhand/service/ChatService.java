package com.secondhand.service;

import com.secondhand.dto.chatroom.ChatroomSummaryResponse;
import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.dto.message.MessageResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.*;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.ChatroomRepository;
import com.secondhand.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing chat rooms and messages between buyers and sellers.
 * <p>
 * A chat room is scoped to a single advertisement: a buyer initiates a conversation
 * with the seller of a given advertisement. Each (buyer, advertisement) pair can have
 * at most one chat room; calling {@link #startOrGetChat} for an existing pair returns
 * the existing room rather than creating a duplicate.
 * </p>
 * <p>
 * Both the buyer and the seller are participants of the chat room and may send messages
 * and view the conversation. Unread message counts are tracked per user per room.
 * </p>
 */
@Service
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final AdvService advService;
    private final UserService userService;

    /**
     * Constructs a {@code ChatService} with all required repository and service dependencies.
     *
     * @param chatroomRepository the repository for persisting and querying {@link Chatroom} entities
     * @param messageRepository  the repository for persisting and querying {@link Message} entities
     * @param advService         the service used to look up advertisements by ID
     * @param userService        the service used to look up users by ID
     */
    public ChatService(ChatroomRepository chatroomRepository,
                       MessageRepository messageRepository,
                       AdvService advService,
                       UserService userService) {
        this.chatroomRepository = chatroomRepository;
        this.messageRepository = messageRepository;
        this.advService = advService;
        this.userService = userService;
    }

    /**
     * Starts a new chat room for the given advertisement or returns the existing one.
     * <p>
     * If a chat room already exists between the requesting user and the advertisement's
     * seller, it is returned without creating a duplicate. Otherwise, both the buyer
     * and the seller are added as participants of a newly created room.
     * </p>
     *
     * @param request the chat creation request containing the advertisement ID
     * @param userId  the UUID of the authenticated user initiating the conversation (buyer)
     * @return a {@link ChatroomDetailResponse} for the new or existing chat room
     * @throws ResourceNotFoundException if no advertisement exists with the given ID
     * @throws ForbiddenException        if the requesting user is the owner of the advertisement
     */
    public ChatroomDetailResponse startOrGetChat(ChatroomCreateRequest request, UUID userId) {
        Adv adv = advService.findAdvById(request.advId());

        if (adv.getUser().getId().equals(userId)) {
            throw new ForbiddenException("شما نمی توانید در آگهی خود، گفت و گویی آغاز کنید");
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

    /**
     * Returns a summary list of all chat rooms the given user participates in.
     * <p>
     * Each summary includes the unread message count for the requesting user in that room.
     * </p>
     *
     * @param userId the UUID of the user whose chat rooms should be retrieved
     * @return a list of {@link ChatroomSummaryResponse} objects; never {@code null}, may be empty
     */
    public List<ChatroomSummaryResponse> getUserChatRooms(UUID userId) {
        List<Chatroom> rooms = chatroomRepository.findByParticipantId(userId);
        return rooms.stream()
                .map(this::toChatroomSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns the full details of a specific chat room and marks all messages as seen
     * for the requesting user.
     *
     * @param chatroomId the UUID of the chat room to retrieve
     * @param userId     the UUID of the authenticated user accessing the room
     * @return a {@link ChatroomDetailResponse} containing the room details and all messages
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the requesting user is not a participant of the room
     */
    public ChatroomDetailResponse getChatroomDetail(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        messageRepository.markAllAsSeen(chatroomId, userId);
        return toChatroomDetailResponse(room);
    }

    /**
     * Sends a new message in the specified chat room.
     * <p>
     * Validates that the requesting user is a participant of the room before
     * creating and persisting the message.
     * </p>
     *
     * @param chatroomId the UUID of the chat room to send the message in
     * @param request    the message data containing the text to send
     * @param userId     the UUID of the authenticated user sending the message
     * @return a {@link MessageResponse} representing the newly sent message
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the requesting user is not a participant of the room
     */
    public MessageResponse sendMessage(UUID chatroomId, MessageRequest request, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        User sender = userService.findUserById(userId);

        Message msg = new Message(request.text(), sender, room);
        room.addMessage(msg);

        Message saved = messageRepository.save(msg);
        return toMessageResponse(saved);
    }

    /**
     * Returns all messages in a chat room in chronological order.
     * <p>
     * Validates that the requesting user is a participant before returning messages.
     * </p>
     *
     * @param chatroomId the UUID of the chat room whose messages should be retrieved
     * @param userId     the UUID of the authenticated user requesting the messages
     * @return a list of {@link MessageResponse} objects ordered by date ascending;
     *         never {@code null}, may be empty
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the requesting user is not a participant of the room
     */
    public List<MessageResponse> getMessages(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        List<Message> messages = messageRepository.findByChatroomIdOrderByDateAsc(chatroomId);
        return messages.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    /**
     * Looks up a chat room by its UUID, throwing if not found.
     *
     * @param chatroomId the UUID of the chat room to find
     * @return the {@link Chatroom} entity with the given ID
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     */
    private Chatroom findChatroomById(UUID chatroomId) {
        return chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new ResourceNotFoundException("گفت و گو یافت نشد"));
    }

    /**
     * Verifies that the given user is a participant of the given chat room.
     * <p>
     * A user qualifies as a participant if they are either the initiating buyer
     * (matched via {@code room.getUserId()}) or the seller of the associated advertisement.
     * </p>
     *
     * @param room   the chat room to check
     * @param userId the UUID of the user to verify as a participant
     * @throws ForbiddenException if the user is neither the buyer nor the seller of the room
     */
    private void checkParticipant(Chatroom room, UUID userId) {
        if (room.getUserId() != null && room.getUserId().equals(userId)) {
            return;
        }

        Adv adv = room.getAdv();
        if (adv != null && adv.getUser() != null && adv.getUser().getId().equals(userId)) {
            return;
        }

        throw new ForbiddenException("شما اجازه شرکت در این گفت و گو را ندارید");
    }

    /**
     * Converts a {@link Chatroom} entity to a lightweight {@link ChatroomSummaryResponse} DTO.
     * <p>
     * Includes the total message count and the count of unseen messages in the room.
     * </p>
     *
     * @param room the chat room entity to convert
     * @return a {@link ChatroomSummaryResponse} with summary fields populated
     */
    private ChatroomSummaryResponse toChatroomSummaryResponse(Chatroom room) {
        return new ChatroomSummaryResponse(
                room.getId(),
                room.getAdv().getId(),
                room.getAdv().getFullName(),
                room.getMessages().size(),
                messageRepository.countByChatroomIdAndSeenFalse(room.getId())
        );
    }

    /**
     * Converts a {@link Chatroom} entity to a full {@link ChatroomDetailResponse} DTO,
     * including the complete list of messages.
     *
     * @param room the chat room entity to convert
     * @return a {@link ChatroomDetailResponse} with all messages populated
     */
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

    /**
     * Converts a {@link Message} entity to a {@link MessageResponse} DTO.
     * <p>
     * Includes the sender's summary information and the seen status of the message.
     * </p>
     *
     * @param msg the message entity to convert
     * @return a {@link MessageResponse} populated with message data and sender summary
     */
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
