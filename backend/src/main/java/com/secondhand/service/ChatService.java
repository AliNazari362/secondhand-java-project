package com.secondhand.service;

import com.secondhand.dto.chatroom.ChatroomSummaryResponse;
import com.secondhand.dto.chatroom.ChatroomDetailResponse;
import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.dto.message.MessageResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.*;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.ChatroomRepository;
import com.secondhand.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing chat rooms and messages exchanged between buyers and sellers.
 *
 * <p>This service encapsulates all business logic related to chat functionality in the
 * secondhand marketplace platform. It handles:
 * <ul>
 *   <li>Creating new chat rooms for a specific advertisement</li>
 *   <li>Retrieving chat rooms for a given user (as buyer or seller)</li>
 *   <li>Sending and retrieving messages within a chat room</li>
 *   <li>Marking messages as seen by participants</li>
 * </ul>
 *
 * <p><strong>Chat Room Scoping:</strong> Each chat room is strictly scoped to a single
 * advertisement. A chat room is created when a buyer initiates a conversation about an
 * advertisement. The same buyer and advertisement pair can have at most one chat room.</p>
 *
 * <p><strong>Ownership Model:</strong> The chat room is owned by the buyer (the user
 * who initiates the conversation). The {@code userId} field in the {@code chatrooms}
 * table is set to the buyer's identifier. The seller is not stored in the {@code rooms}
 * collection of any user but is identified through the associated advertisement's
 * owner ({@code adv.user}). Both parties can participate in the chat room.</p>
 *
 * <p><strong>Access Control:</strong> Only the buyer (chat room owner) and the seller
 * (advertisement owner) are allowed to access the chat room, send messages, and view
 * the conversation history. This is enforced by the {@link #checkParticipant} method.</p>
 *
 * <p><strong>Advertisement Status Validation:</strong> Chat rooms can only be created
 * for advertisements that are either {@link AdvStatus#ACTIVE} or {@link AdvStatus#SOLD}.
 * Attempting to start a chat for a {@link AdvStatus#PENDING}, {@link AdvStatus#REJECTED},
 * or {@link AdvStatus#DELETED} advertisement will result in a {@link BadRequestException}.</p>
 *
 * <p><strong>Transactional Behavior:</strong> All public methods in this service are
 * marked with {@code @Transactional} to ensure atomicity. Operations involving multiple
 * entities (e.g., creating a chat room and updating a user's room list) are performed
 * as a single transaction, rolling back on failure.</p>
 *
 * @see Chatroom
 * @see Message
 * @see Adv
 * @see User
 */
@Service
@Transactional
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final AdvService advService;
    private final UserService userService;

    /**
     * Constructs a {@code ChatService} with all required repository and service dependencies.
     *
     * <p>All dependencies are injected via constructor injection, which is the recommended
     * practice for Spring beans. This approach makes the service easier to test and
     * ensures that all dependencies are required and immutable.</p>
     *
     * @param chatroomRepository the repository for persisting and querying {@link Chatroom} entities
     * @param messageRepository  the repository for persisting and querying {@link Message} entities
     * @param advService         the service used to look up advertisements by ID and verify their status
     * @param userService        the service used to look up and persist user entities
     * @throws NullPointerException if any of the injected dependencies are {@code null}
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
     * Starts a new chat room for the specified advertisement or returns the existing one.
     *
     * <p>This method implements the core logic for initiating a conversation between a
     * buyer and a seller about a specific advertisement. The process follows these steps:</p>
     * <ol>
     *   <li>Validates that the advertisement exists and is in a valid status
     *       ({@code ACTIVE} or {@code SOLD}).</li>
     *   <li>Ensures the user is not trying to chat about their own advertisement.</li>
     *   <li>Checks whether a chat room already exists for this buyer and advertisement pair.</li>
     *   <li>If an existing chat room is found, it is returned immediately.</li>
     *   <li>If no chat room exists, a new one is created, associated with the buyer,
     *       and persisted.</li>
     * </ol>
     *
     * <p><strong>Important:</strong> Only the buyer is added to the {@code rooms} collection.
     * The seller is not added to any user's room list. Access is granted to the seller
     * through the {@code adv.user} association, as implemented in {@link #checkParticipant}.</p>
     *
     * <p><strong>Error Conditions:</strong></p>
     * <ul>
     *   <li>{@link ResourceNotFoundException} – if the advertisement does not exist.</li>
     *   <li>{@link ForbiddenException} – if the user tries to chat about their own advertisement.</li>
     *   <li>{@link BadRequestException} – if the advertisement is not in {@code ACTIVE} or {@code SOLD} status.</li>
     * </ul>
     *
     * @param request the chat creation request containing the advertisement identifier
     * @param userId  the UUID of the authenticated user initiating the conversation (the buyer)
     * @return a {@link ChatroomDetailResponse} containing the details of the new or existing chat room
     * @throws ResourceNotFoundException if no advertisement exists with the given ID
     * @throws ForbiddenException        if the user is the owner of the advertisement
     * @throws BadRequestException       if the advertisement is not in a valid status for chat
     */
    public ChatroomDetailResponse startOrGetChat(ChatroomCreateRequest request, UUID userId) {
        Adv adv = advService.findAdvById(request.advId());

        if (adv.getStatus() != AdvStatus.ACTIVE && adv.getStatus() != AdvStatus.SOLD) {
            throw new BadRequestException("امکان شروع گفت‌وگو برای این آگهی وجود ندارد");
        }

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
        Chatroom newRoom = new Chatroom(adv);

        buyer.addRoom(newRoom);
        Chatroom saved = chatroomRepository.save(newRoom);
        userService.saveUser(buyer);

        return toChatroomDetailResponse(saved);
    }

    /**
     * Retrieves a summary list of all chat rooms in which the given user participates.
     *
     * <p>A user participates in a chat room if they are either the buyer (owner of the
     * chat room) or the seller (owner of the associated advertisement). The result set
     * includes the following summary information for each chat room:</p>
     * <ul>
     *   <li>Chat room identifier</li>
     *   <li>Associated advertisement identifier and title</li>
     *   <li>Total number of messages in the chat room</li>
     *   <li>Number of unread messages for the requesting user</li>
     * </ul>
     *
     * <p>The list is intended for display in a user interface where users can view
     * and select their active conversations.</p>
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
     * Retrieves the full details of a specific chat room, including all messages,
     * and marks all messages as seen for the requesting user.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *   <li>Finds the chat room by its UUID.</li>
     *   <li>Verifies that the requesting user is a participant (buyer or seller).</li>
     *   <li>Marks all messages in the chat room as {@code seen = true} for the current user,
     *       excluding messages sent by that user (a user should not mark their own outgoing
     *       messages as seen).</li>
     *   <li>Returns the full chat room details, including the complete message history
     *       ordered chronologically.</li>
     * </ol>
     *
     * <p><strong>Error Conditions:</strong></p>
     * <ul>
     *   <li>{@link ResourceNotFoundException} – if no chat room exists with the given ID.</li>
     *   <li>{@link ForbiddenException} – if the requesting user is not a participant.</li>
     * </ul>
     *
     * @param chatroomId the UUID of the chat room to retrieve
     * @param userId     the UUID of the authenticated user accessing the room
     * @return a {@link ChatroomDetailResponse} containing the room details and all messages
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the requesting user is not a participant
     */
    public ChatroomDetailResponse getChatroomDetail(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        messageRepository.markAllAsSeen(chatroomId, userId);
        return toChatroomDetailResponse(room);
    }

    /**
     * Sends a new message in the specified chat room on behalf of the authenticated user.
     *
     * <p>This method validates that the sender is a participant in the chat room before
     * creating and persisting the message. The message is associated with the chat room
     * and the sender, and includes a timestamp. The message is initially marked as
     * {@code seen = false} until the recipient views it.</p>
     *
     * <p><strong>Error Conditions:</strong></p>
     * <ul>
     *   <li>{@link ResourceNotFoundException} – if no chat room exists with the given ID.</li>
     *   <li>{@link ForbiddenException} – if the sender is not a participant.</li>
     * </ul>
     *
     * @param chatroomId the UUID of the chat room to send the message in
     * @param request    the message data containing the text to send
     * @param userId     the UUID of the authenticated user sending the message
     * @return a {@link MessageResponse} representing the newly sent message
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the user is not a participant
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
     * Retrieves all messages in a chat room in chronological order (oldest first).
     *
     * <p>This method provides the full conversation history for a given chat room.
     * Messages are ordered by date ascending, which means the oldest messages appear
     * first and the newest messages appear last. This is the natural reading order
     * for a chat application.</p>
     *
     * <p>Each message includes the sender's summary information, the message text,
     * the timestamp, and a flag indicating whether the message has been seen by the
     * recipient.</p>
     *
     * <p><strong>Error Conditions:</strong></p>
     * <ul>
     *   <li>{@link ResourceNotFoundException} – if no chat room exists with the given ID.</li>
     *   <li>{@link ForbiddenException} – if the user is not a participant.</li>
     * </ul>
     *
     * @param chatroomId the UUID of the chat room whose messages should be retrieved
     * @param userId     the UUID of the authenticated user requesting the messages
     * @return a list of {@link MessageResponse} objects ordered by date ascending;
     *         never {@code null}, may be empty
     * @throws ResourceNotFoundException if no chat room exists with the given {@code chatroomId}
     * @throws ForbiddenException        if the user is not a participant
     */
    public List<MessageResponse> getMessages(UUID chatroomId, UUID userId) {
        Chatroom room = findChatroomById(chatroomId);
        checkParticipant(room, userId);

        List<Message> messages = messageRepository.findByChatroomIdOrderByDateAsc(chatroomId);

        messages.stream()
                .filter(msg -> !msg.isSeen() && !msg.getSender().getId().equals(userId))
                .forEach(msg -> {
                    msg.setSeen(true);
                });

        messageRepository.saveAll(messages);
        return messages.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Private Helper Methods
    // -------------------------------------------------------------------------

    /**
     * Finds a chat room by its UUID, throwing an exception if not found.
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
     * Verifies that the given user is a participant in the given chat room.
     *
     * <p>A user is considered a participant if one of the following conditions is met:</p>
     * <ul>
     *   <li>The user is the buyer (owner) of the chat room, identified by {@code room.getUserId()}.</li>
     *   <li>The user is the seller, identified by {@code room.getAdv().getUser().getId()}.</li>
     * </ul>
     *
     * @param room   the chat room to check
     * @param userId the UUID of the user to verify as a participant
     * @throws ForbiddenException if the user is neither the buyer nor the seller
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

    // -------------------------------------------------------------------------
    // DTO Mappers
    // -------------------------------------------------------------------------

    /**
     * Converts a {@link Chatroom} entity to a lightweight {@link ChatroomSummaryResponse} DTO.
     *
     * <p>The summary includes the total message count and the count of unseen messages
     * in the room, which are calculated using the {@link MessageRepository}.</p>
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
     * <p>Each message is converted using {@link #toMessageResponse} to include sender
     * summary information.</p>
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
     *
     * <p>The sender's summary information is embedded as a {@link UserSummaryResponse}
     * to avoid circular references and expose only public profile data. The {@code seen}
     * flag indicates whether the message has been read by the recipient.</p>
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