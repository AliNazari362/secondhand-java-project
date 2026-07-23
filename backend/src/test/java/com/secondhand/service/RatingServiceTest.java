/**
 * Unit tests for {@link RatingService}.
 * Tests rating operations for sellers, including creating ratings and retrieving statistics.
 */
package com.secondhand.service;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Comment;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link RatingService}.
 * Verifies the correct behavior of rating-related operations including
 * submitting a rating, retrieving average rating, and retrieving rating count.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    /** Mocked repository for comment data access. */
    @Mock
    private CommentRepository commentRepository;

    /** Mocked service for advertisement operations. */
    @Mock
    private AdvService advService;

    /** Mocked service for user operations. */
    @Mock
    private UserService userService;

    /** Mocked service for comment operations. */
    @Mock
    private CommentService commentService;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private RatingService ratingService;

    // ==================== TEST FIXTURES ====================

    /** User ID. */
    private UUID userId;

    /** Advertisement ID. */
    private UUID advId;

    /** Test user instance. */
    private User testUser;

    /** Another user instance (the seller). */
    private User otherUser;

    /** Test advertisement. */
    private Adv testAdv;

    /** Rating request. */
    private CommentRequest ratingRequest;

    /**
     * Sets up common test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        advId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setFullName("Hessam Test");
        testUser.setEmail("hessam@example.com");
        testUser.setUserType(UserType.USER);

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setFullName("Other User");

        testAdv = new Product();
        testAdv.setId(advId);
        testAdv.setFullName("Test Product");
        testAdv.setStatus(AdvStatus.ACTIVE);
        testAdv.setAdvType(AdvType.PRODUCT);
        testAdv.setCity(City.TEHRAN);
        testAdv.setUser(otherUser);

        ratingRequest = new CommentRequest("Good seller!", 4);
    }

    // ==================== RATE ADVERTISEMENT TESTS ====================

    /**
     * Tests that a rating is successfully submitted when the request is valid.
     * Verifies that the rating is saved in the repository.
     */
    @Test
    void rateAdvertisement_ShouldSucceed_WhenValid() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(false);

        Comment savedComment = new Comment();
        savedComment.setText("Good seller!");
        savedComment.setRate(4);
        savedComment.setUser(testUser);
        savedComment.setAdv(testAdv);

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponse mockResponse = new CommentResponse(
                1L,
                "Good seller!",
                4,
                new UserSummaryResponse(testUser.getId(), testUser.getFullName(), testUser.getEmail(), testUser.getUserType()),
                LocalDateTime.now()
        );
        when(commentService.toCommentResponse(any(Comment.class))).thenReturn(mockResponse);

        var response = ratingService.rateAdvertisement(advId, ratingRequest, userId);

        assertNotNull(response);
        assertEquals("Good seller!", response.text());
        assertEquals(4, response.rate());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Tests that rating fails when the user is the owner of the advertisement.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void rateAdvertisement_ShouldThrowException_WhenUserOwnsAdvertisement() {
        testAdv.setUser(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ForbiddenException.class,
                () -> ratingService.rateAdvertisement(advId, ratingRequest, userId));
    }

    /**
     * Tests that rating fails when the user has already rated this seller for this advertisement.
     * Expects a {@link ResourceAlreadyExistsException}.
     */
    @Test
    void rateAdvertisement_ShouldThrowException_WhenAlreadyRated() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> ratingService.rateAdvertisement(advId, ratingRequest, userId));
    }

    // ==================== GET AVERAGE RATING TESTS ====================

    /**
     * Tests that the average rating for an advertisement is correctly retrieved.
     */
    @Test
    void getAverageRating_ShouldReturnDouble() {
        when(commentRepository.getAverageRatingByAdvId(advId)).thenReturn(4.5);

        double avg = ratingService.getAverageRating(advId);

        assertEquals(4.5, avg);
    }

    /**
     * Tests that retrieving the average rating returns zero when no ratings exist.
     */
    @Test
    void getAverageRating_ShouldReturnZero_WhenNoRatings() {
        when(commentRepository.getAverageRatingByAdvId(advId)).thenReturn(null);

        double avg = ratingService.getAverageRating(advId);

        assertEquals(0.0, avg);
    }

    // ==================== GET RATING COUNT TESTS ====================

    /**
     * Tests that the rating count for an advertisement is correctly retrieved.
     */
    @Test
    void getRatingCount_ShouldReturnLong() {
        when(commentRepository.countByAdvId(advId)).thenReturn(5L);

        long count = ratingService.getRatingCount(advId);

        assertEquals(5L, count);
    }
}