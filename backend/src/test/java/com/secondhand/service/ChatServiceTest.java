/**
 * Unit tests for {@link ChatService}.
 * Tests chat creation, message sending, and chat retrieval operations.
 */
package com.secondhand.service;

import com.secondhand.dto.chatroom.ChatroomCreateRequest;
import com.secondhand.dto.message.MessageRequest;
import com.secondhand.entity.*;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.repository.ChatroomRepository;
import com.secondhand.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link ChatService}.
 * Verifies the correct behavior of chat-related operations including
 * starting a chat, sending messages, retrieving chat lists, and marking messages as seen.
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    /** Mocked repository for chatroom data access. */
    @Mock
    private ChatroomRepository chatroomRepository;

    /** Mocked repository for message data access. */
    @Mock
    private MessageRepository messageRepository;

    /** Mocked service for advertisement operations. */
    @Mock
    private AdvService advService;

    /** Mocked service for user operations. */
    @Mock
    private UserService userService;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private ChatService chatService;

    // ==================== TEST FIXTURES ====================

    /** Buyer user ID. */
    private UUID buyerId;

    /** Seller user ID. */
    private UUID sellerId;

    /** Advertisement ID. */
    private UUID advId;

    /** Chatroom ID. */
    private UUID chatroomId;

    /** Buyer user instance. */
    private User buyer;

    /** Seller user instance. */
    private User seller;

    /** Test advertisement. */
    private Adv testAdv;

    /** Test chatroom instance. */
    private Chatroom testChatroom;

    /** Test message instance. */
    private Message testMessage;

    /** Chat creation request. */
    private ChatroomCreateRequest createRequest;

    /** Message request. */
    private MessageRequest messageRequest;

    /**
     * Sets up common test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        buyerId = UUID.randomUUID();
        sellerId = UUID.randomUUID();
        advId = UUID.randomUUID();
        chatroomId = UUID.randomUUID();

        buyer = new User();
        buyer.setId(buyerId);
        buyer.setFullName("Buyer User");
        buyer.setEmail("buyer@example.com");
        buyer.setUserType(UserType.USER);

        seller = new User();
        seller.setId(sellerId);
        seller.setFullName("Seller User");
        seller.setEmail("seller@example.com");
        seller.setUserType(UserType.USER);

        testAdv = new Product();
        testAdv.setId(advId);
        testAdv.setFullName("Test Product");
        testAdv.setStatus(AdvStatus.ACTIVE);
        testAdv.setAdvType(AdvType.PRODUCT);
        testAdv.setCity(City.TEHRAN);
        testAdv.setUser(seller);

        testChatroom = new Chatroom(testAdv);
        ReflectionTestUtils.setField(testChatroom, "id", chatroomId);
        // Add userId for the buyer to be used in participant tests
        ReflectionTestUtils.setField(testChatroom, "userId", buyerId);

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setText("Hello!");
        testMessage.setSender(buyer);
        testMessage.setChatroom(testChatroom);

        createRequest = new ChatroomCreateRequest(advId);
        messageRequest = new MessageRequest("Hello!");
    }

    // ==================== START CHAT TESTS ====================

    /**
     * Tests that a new chatroom is created when one does not already exist for the given
     * advertisement and buyer.
     */
    @Test
    void startOrGetChat_ShouldCreateNewChat_WhenNotExists() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(chatroomRepository.findByUserIdAndAdvId(buyerId, advId))
                .thenReturn(Optional.empty());
        when(userService.findUserById(buyerId)).thenReturn(buyer);
        when(chatroomRepository.save(any(Chatroom.class))).thenReturn(testChatroom);

        var response = chatService.startOrGetChat(createRequest, buyerId);

        assertNotNull(response);
        assertEquals(testChatroom.getId(), response.id());
        assertEquals(testAdv.getFullName(), response.advTitle());
        verify(chatroomRepository).save(any(Chatroom.class));
        verify(userService).saveUser(buyer);
    }

    /**
     * Tests that an existing chatroom is returned when one already exists for the given
     * advertisement and buyer.
     */
    @Test
    void startOrGetChat_ShouldReturnExistingChat_WhenExists() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(chatroomRepository.findByUserIdAndAdvId(buyerId, advId))
                .thenReturn(Optional.of(testChatroom));

        var response = chatService.startOrGetChat(createRequest, buyerId);

        assertNotNull(response);
        assertEquals(testChatroom.getId(), response.id());
        verify(chatroomRepository, never()).save(any(Chatroom.class));
    }

    /**
     * Tests that starting a chat fails when the advertisement is not active or sold.
     * Expects a {@link BadRequestException}.
     */
    @Test
    void startOrGetChat_ShouldThrowException_WhenAdvNotActiveOrSold() {
        testAdv.setStatus(AdvStatus.PENDING);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(BadRequestException.class,
                () -> chatService.startOrGetChat(createRequest, buyerId));
    }

    /**
     * Tests that starting a chat fails when the buyer is the owner of the advertisement.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void startOrGetChat_ShouldThrowException_WhenUserOwnsAdv() {
        testAdv.setUser(buyer);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ForbiddenException.class,
                () -> chatService.startOrGetChat(createRequest, buyerId));
    }

    // ==================== GET CHATROOMS TESTS ====================

    /**
     * Tests that retrieving chatrooms for a user returns a list of their chatrooms.
     */
    @Test
    void getUserChatRooms_ShouldReturnList() {
        when(chatroomRepository.findByParticipantId(buyerId))
                .thenReturn(List.of(testChatroom));

        var results = chatService.getUserChatRooms(buyerId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testChatroom.getId(), results.get(0).id());
    }

    // ==================== CHATROOM DETAIL TESTS ====================

    /**
     * Tests that retrieving chatroom details succeeds when the user is the buyer participant.
     * Verifies that messages are marked as seen.
     */
    @Test
    void getChatroomDetail_ShouldReturnDetails_WhenUserIsBuyer() {
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        var response = chatService.getChatroomDetail(chatroomId, buyerId);

        assertNotNull(response);
        verify(messageRepository).markAllAsSeen(chatroomId, buyerId);
    }

    /**
     * Tests that retrieving chatroom details succeeds when the user is the seller participant.
     */
    @Test
    void getChatroomDetail_ShouldReturnDetails_WhenUserIsSeller() {
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        var response = chatService.getChatroomDetail(chatroomId, sellerId);

        assertNotNull(response);
        verify(messageRepository).markAllAsSeen(chatroomId, sellerId);
    }

    /**
     * Tests that retrieving chatroom details fails when the user is not a participant.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void getChatroomDetail_ShouldThrowException_WhenUserNotParticipant() {
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.getChatroomDetail(chatroomId, otherUserId));
    }

    // ==================== SEND MESSAGE TESTS ====================

    /**
     * Tests that sending a message succeeds when the user is a participant in the chatroom.
     */
    @Test
    void sendMessage_ShouldSucceed_WhenUserIsParticipant() {
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));
        when(userService.findUserById(buyerId)).thenReturn(buyer);
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        var response = chatService.sendMessage(chatroomId, messageRequest, buyerId);

        assertNotNull(response);
        assertEquals(testMessage.getText(), response.text());
        assertEquals(buyer.getFullName(), response.sender().fullName());
        verify(messageRepository).save(any(Message.class));
    }

    /**
     * Tests that sending a message fails when the user is not a participant in the chatroom.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void sendMessage_ShouldThrowException_WhenUserNotParticipant() {
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.sendMessage(chatroomId, messageRequest, otherUserId));
    }

    // ==================== GET MESSAGES TESTS ====================

    /**
     * Tests that retrieving messages for a chatroom returns a list when the user is a participant.
     */
    @Test
    void getMessages_ShouldReturnList_WhenUserIsParticipant() {
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));
        when(messageRepository.findByChatroomIdOrderByDateAsc(chatroomId))
                .thenReturn(List.of(testMessage));

        var results = chatService.getMessages(chatroomId, buyerId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testMessage.getText(), results.get(0).text());
    }

    /**
     * Tests that retrieving messages fails when the user is not a participant.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void getMessages_ShouldThrowException_WhenUserNotParticipant() {
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.getMessages(chatroomId, otherUserId));
    }
}