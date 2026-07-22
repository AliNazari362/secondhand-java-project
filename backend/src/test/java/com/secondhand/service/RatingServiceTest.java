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

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AdvService advService;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private RatingService ratingService;

    private UUID userId;
    private UUID advId;
    private User testUser;
    private User otherUser;
    private Adv testAdv;
    private CommentRequest ratingRequest;

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

    @Test
    void rateAdvertisement_ShouldSucceed_WhenValid() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(false);

        // ✅ یک Comment بدون نیاز به setId بسازید
        Comment savedComment = new Comment();
        savedComment.setText("Good seller!");
        savedComment.setRate(4);
        savedComment.setUser(testUser);
        savedComment.setAdv(testAdv);
        // نیازی به تنظیم تاریخ و id نیست چون toCommentResponse را Mock می‌کنیم

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // ✅ Mock کردن toCommentResponse به جای thenCallRealMethod
        CommentResponse mockResponse = new CommentResponse(
                1L, // id دلخواه
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

    @Test
    void rateAdvertisement_ShouldThrowException_WhenUserOwnsAdvertisement() {
        testAdv.setUser(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ForbiddenException.class,
                () -> ratingService.rateAdvertisement(advId, ratingRequest, userId));
    }

    @Test
    void rateAdvertisement_ShouldThrowException_WhenAlreadyRated() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> ratingService.rateAdvertisement(advId, ratingRequest, userId));
    }

    @Test
    void getAverageRating_ShouldReturnDouble() {
        when(commentRepository.getAverageRatingByAdvId(advId)).thenReturn(4.5);

        double avg = ratingService.getAverageRating(advId);

        assertEquals(4.5, avg);
    }

    @Test
    void getAverageRating_ShouldReturnZero_WhenNoRatings() {
        when(commentRepository.getAverageRatingByAdvId(advId)).thenReturn(null);

        double avg = ratingService.getAverageRating(advId);

        assertEquals(0.0, avg);
    }

    @Test
    void getRatingCount_ShouldReturnLong() {
        when(commentRepository.countByAdvId(advId)).thenReturn(5L);

        long count = ratingService.getRatingCount(advId);

        assertEquals(5L, count);
    }
}