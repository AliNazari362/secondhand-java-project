package com.secondhand.service;

import com.secondhand.dto.admin.DashboardStatsResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.repository.AdvRepository;
import com.secondhand.repository.CommentRepository;
import com.secondhand.repository.MessageRepository;
import com.secondhand.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdvService advService;

    @Mock
    private AdvRepository advRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private AdminService adminService;

    private UUID userId;
    private User testUser;
    private Adv testAdv;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setFullName("Hessam Test");
        testUser.setEmail("hessam@example.com");
        testUser.setUserType(UserType.USER);
        testUser.setUserStatus(UserStatus.ACTIVE);

        testAdv = new Product();
        testAdv.setId(UUID.randomUUID());
        testAdv.setFullName("Test Product");
        testAdv.setStatus(AdvStatus.PENDING);
        testAdv.setAdvType(AdvType.PRODUCT);
        testAdv.setCity(City.TEHRAN);
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userService.toUserSummaryResponse(any(User.class))).thenCallRealMethod();

        var results = adminService.getAllUsers();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testUser.getFullName(), results.get(0).fullName());
    }

    @Test
    void getUsersByStatus_ShouldReturnFilteredList() {
        when(userRepository.findByUserStatus(UserStatus.ACTIVE)).thenReturn(List.of(testUser));
        when(userService.toUserSummaryResponse(any(User.class))).thenCallRealMethod();

        var results = adminService.getUsersByStatus(UserStatus.ACTIVE);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void banUser_ShouldSetStatusToBanned() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        adminService.banUser(userId);

        assertEquals(UserStatus.BANNED, testUser.getUserStatus());
        verify(userRepository).save(testUser);
    }

    @Test
    void unbanUser_ShouldSetStatusToActive() {
        testUser.setUserStatus(UserStatus.BANNED);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        adminService.unbanUser(userId);

        assertEquals(UserStatus.ACTIVE, testUser.getUserStatus());
        verify(userRepository).save(testUser);
    }

    @Test
    void approveAdv_ShouldDelegateToAdvService() {
        adminService.approveAdv(testAdv.getId());
        verify(advService).approveAdv(testAdv.getId());
    }

    @Test
    void rejectAdv_ShouldDelegateToAdvService() {
        adminService.rejectAdv(testAdv.getId(), "Invalid");
        verify(advService).rejectAdv(testAdv.getId(), "Invalid");
    }

    @Test
    void deleteAdv_ShouldSetStatusToDeleted() {
        when(advService.findAdvById(testAdv.getId())).thenReturn(testAdv);
        when(advRepository.save(any(Adv.class))).thenReturn(testAdv);

        adminService.deleteAdv(testAdv.getId());

        assertEquals(AdvStatus.DELETED, testAdv.getStatus());
        verify(advRepository).save(testAdv);
    }

    @Test
    void getPendingAds_ShouldDelegateToAdvService() {
        adminService.getPendingAds();
        verify(advService).getPendingAds();
    }

    @Test
    void getDashboardStats_ShouldReturnAllStatistics() {
        // Arrange - User stats
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.findByUserStatus(UserStatus.ACTIVE)).thenReturn(List.of(testUser));
        when(userRepository.findByUserStatus(UserStatus.BANNED)).thenReturn(List.of());
        when(userRepository.findByUserStatus(UserStatus.DELETED)).thenReturn(List.of());

        // Arrange - Adv stats
        when(advRepository.count()).thenReturn(20L);
        when(advRepository.findByStatus(AdvStatus.PENDING)).thenReturn(List.of(testAdv));
        when(advRepository.findByStatus(AdvStatus.ACTIVE)).thenReturn(List.of());
        when(advRepository.findByStatus(AdvStatus.SOLD)).thenReturn(List.of());
        when(advRepository.findByStatus(AdvStatus.REJECTED)).thenReturn(List.of());

        // Arrange - Message & Comment stats
        when(messageRepository.count()).thenReturn(100L);
        when(commentRepository.count()).thenReturn(50L);

        // Act
        DashboardStatsResponse stats = adminService.getDashboardStats();

        // Assert
        assertNotNull(stats);
        assertEquals(10L, stats.totalUsers());
        assertEquals(1L, stats.activeUsers());
        assertEquals(0L, stats.bannedUsers());
        assertEquals(0L, stats.deletedUsers());
        assertEquals(20L, stats.totalAds());
        assertEquals(1L, stats.pendingAds());
        assertEquals(0L, stats.activeAds());
        assertEquals(0L, stats.soldAds());
        assertEquals(0L, stats.rejectedAds());
        assertEquals(100L, stats.totalMessages());
        assertEquals(50L, stats.totalComments());
    }
}