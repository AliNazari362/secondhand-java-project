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

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatroomRepository chatroomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AdvService advService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    private UUID buyerId;
    private UUID sellerId;
    private UUID advId;
    private UUID chatroomId;
    private User buyer;
    private User seller;
    private Adv testAdv;
    private Chatroom testChatroom;
    private Message testMessage;
    private ChatroomCreateRequest createRequest;
    private MessageRequest messageRequest;

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
        // ✅ اضافه کردن userId برای خریدار (برای تست‌های شرکت‌کننده)
        ReflectionTestUtils.setField(testChatroom, "userId", buyerId);

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setText("Hello!");
        testMessage.setSender(buyer);
        testMessage.setChatroom(testChatroom);

        createRequest = new ChatroomCreateRequest(advId);
        messageRequest = new MessageRequest("Hello!");
    }

    // ---------- تست‌های مربوط به startOrGetChat ----------

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

    @Test
    void startOrGetChat_ShouldThrowException_WhenAdvNotActiveOrSold() {
        testAdv.setStatus(AdvStatus.PENDING);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(BadRequestException.class,
                () -> chatService.startOrGetChat(createRequest, buyerId));
    }

    @Test
    void startOrGetChat_ShouldThrowException_WhenUserOwnsAdv() {
        // این تست ۴ هست
        testAdv.setUser(buyer);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ForbiddenException.class,
                () -> chatService.startOrGetChat(createRequest, buyerId));
    }

    // ---------- تست‌های مربوط به getUserChatRooms ----------

    @Test
    void getUserChatRooms_ShouldReturnList() {
        when(chatroomRepository.findByParticipantId(buyerId))
                .thenReturn(List.of(testChatroom));

        var results = chatService.getUserChatRooms(buyerId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testChatroom.getId(), results.get(0).id());
    }

    // ---------- تست‌های مربوط به getChatroomDetail ----------

    @Test
    void getChatroomDetail_ShouldReturnDetails_WhenUserIsBuyer() {
        // تست ۸
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        var response = chatService.getChatroomDetail(chatroomId, buyerId);

        assertNotNull(response);
        verify(messageRepository).markAllAsSeen(chatroomId, buyerId);
    }

    @Test
    void getChatroomDetail_ShouldReturnDetails_WhenUserIsSeller() {
        // این تست رو هم اضافه کردم برای پوشش فروشنده
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        var response = chatService.getChatroomDetail(chatroomId, sellerId);

        assertNotNull(response);
        verify(messageRepository).markAllAsSeen(chatroomId, sellerId);
    }

    @Test
    void getChatroomDetail_ShouldThrowException_WhenUserNotParticipant() {
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.getChatroomDetail(chatroomId, otherUserId));
    }

    // ---------- تست‌های مربوط به sendMessage ----------

    @Test
    void sendMessage_ShouldSucceed_WhenUserIsParticipant() {
        // تست ۹ (قبلاً ۸ بود)
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

    @Test
    void sendMessage_ShouldThrowException_WhenUserNotParticipant() {
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.sendMessage(chatroomId, messageRequest, otherUserId));
    }

    // ---------- تست‌های مربوط به getMessages ----------

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

    @Test
    void getMessages_ShouldThrowException_WhenUserNotParticipant() {
        // تست ۱۰
        UUID otherUserId = UUID.randomUUID();
        when(chatroomRepository.findById(chatroomId))
                .thenReturn(Optional.of(testChatroom));

        assertThrows(ForbiddenException.class,
                () -> chatService.getMessages(chatroomId, otherUserId));
    }
}