/**
 * Unit tests for {@link AdminService}.
 * Tests administrative operations including user management, advertisement
 * moderation, and dashboard statistics.
 */
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

/**
 * Test class for {@link AdminService}.
 * Verifies the correct behavior of administrative operations such as
 * retrieving users, banning/unbanning users, and managing advertisements.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    // ==================== MOCK DEPENDENCIES ====================

    /** Mocked repository for user data access. */
    @Mock
    private UserRepository userRepository;

    /** Mocked service for advertisement-related operations. */
    @Mock
    private AdvService advService;

    /** Mocked repository for advertisement data access. */
    @Mock
    private AdvRepository advRepository;

    /** Mocked service for user-related operations. */
    @Mock
    private UserService userService;

    /** Mocked repository for comment data access. */
    @Mock
    private CommentRepository commentRepository;

    /** Mocked repository for message data access. */
    @Mock
    private MessageRepository messageRepository;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private AdminService adminService;

    // ==================== TEST FIXTURES ====================

    /** Test user ID. */
    private UUID userId;

    /** Test user instance. */
    private User testUser;

    /** Test advertisement instance. */
    private Adv testAdv;

    /**
     * Sets up common test fixtures before each test.
     * Initializes a test user and a test advertisement.
     */
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

    // ==================== USER MANAGEMENT TESTS ====================

    /**
     * Tests that {@link AdminService#getAllUsers()} returns a list of all users.
     * Verifies that the repository is queried and the response is correctly mapped.
     */
    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userService.toUserSummaryResponse(any(User.class))).thenCallRealMethod();

        var results = adminService.getAllUsers();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testUser.getFullName(), results.get(0).fullName());
    }

    /**
     * Tests that {@link AdminService#getUsersByStatus(UserStatus)} returns a filtered
     * list of users based on their status.
     */
    @Test
    void getUsersByStatus_ShouldReturnFilteredList() {
        when(userRepository.findByUserStatus(UserStatus.ACTIVE)).thenReturn(List.of(testUser));
        when(userService.toUserSummaryResponse(any(User.class))).thenCallRealMethod();

        var results = adminService.getUsersByStatus(UserStatus.ACTIVE);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    /**
     * Tests that {@link AdminService#banUser(UUID)} sets the user's status to BANNED.
     */
    @Test
    void banUser_ShouldSetStatusToBanned() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        adminService.banUser(userId);

        assertEquals(UserStatus.BANNED, testUser.getUserStatus());
        verify(userRepository).save(testUser);
    }

    /**
     * Tests that {@link AdminService#unbanUser(UUID)} sets the user's status to ACTIVE
     * when they are currently banned.
     */
    @Test
    void unbanUser_ShouldSetStatusToActive() {
        testUser.setUserStatus(UserStatus.BANNED);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        adminService.unbanUser(userId);

        assertEquals(UserStatus.ACTIVE, testUser.getUserStatus());
        verify(userRepository).save(testUser);
    }

    // ==================== ADVERTISEMENT MANAGEMENT TESTS ====================

    /**
     * Tests that {@link AdminService#approveAdv(UUID)} delegates the operation
     * to {@link AdvService#approveAdv(UUID)}.
     */
    @Test
    void approveAdv_ShouldDelegateToAdvService() {
        adminService.approveAdv(testAdv.getId());
        verify(advService).approveAdv(testAdv.getId());
    }

    /**
     * Tests that {@link AdminService#rejectAdv(UUID, String)} delegates the operation
     * to {@link AdvService#rejectAdv(UUID, String)}.
     */
    @Test
    void rejectAdv_ShouldDelegateToAdvService() {
        adminService.rejectAdv(testAdv.getId(), "Invalid");
        verify(advService).rejectAdv(testAdv.getId(), "Invalid");
    }

    /**
     * Tests that {@link AdminService#deleteAdv(UUID)} sets the advertisement's
     * status to DELETE.
     */
    @Test
    void deleteAdv_ShouldSetStatusToDeleted() {
        when(advService.findAdvById(testAdv.getId())).thenReturn(testAdv);
        when(advRepository.save(any(Adv.class))).thenReturn(testAdv);

        adminService.deleteAdv(testAdv.getId());

        assertEquals(AdvStatus.DELETED, testAdv.getStatus());
        verify(advRepository).save(testAdv);
    }

    /**
     * Tests that {@link AdminService#getPendingAds()} delegates the operation
     * to {@link AdvService#getPendingAds()}.
     */
    @Test
    void getPendingAds_ShouldDelegateToAdvService() {
        adminService.getPendingAds();
        verify(advService).getPendingAds();
    }

    // ==================== DASHBOARD STATISTICS TESTS ====================

    /**
     * Tests that {@link AdminService#getDashboardStats()} aggregates and returns
     * all system statistics correctly.
     * Verifies user counts, advertisement counts, and message/comment counts.
     */
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