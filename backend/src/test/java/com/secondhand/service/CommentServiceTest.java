/**
 * Unit tests for {@link CommentService}.
 * Tests comment creation, updating, deletion, and retrieval operations.
 */
package com.secondhand.service;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentUpdateRequest;
import com.secondhand.entity.*;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.CommentRepository;
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
 * Test class for {@link CommentService}.
 * Verifies the correct behavior of comment-related operations including
 * creating, updating, deleting, and retrieving comments on advertisements.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    /** Mocked repository for comment data access. */
    @Mock
    private CommentRepository commentRepository;

    /** Mocked service for advertisement operations. */
    @Mock
    private AdvService advService;

    /** Mocked service for user operations. */
    @Mock
    private UserService userService;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private CommentService commentService;

    // ==================== TEST FIXTURES ====================

    /** User ID. */
    private UUID userId;

    /** Advertisement ID. */
    private UUID advId;

    /** Test user instance. */
    private User testUser;

    /** Another user instance (the advertisement owner). */
    private User otherUser;

    /** Test advertisement. */
    private Adv testAdv;

    /** Test comment instance. */
    private Comment testComment;

    /** Comment creation request. */
    private CommentRequest commentRequest;

    /** Comment update request. */
    private CommentUpdateRequest updateRequest;

    /**
     * Sets up common test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        advId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
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

        testComment = new Comment();
        ReflectionTestUtils.setField(testComment, "id", 1L);
        testComment.setText("Great product!");
        testComment.setRate(5);
        testComment.setUser(testUser);
        testComment.setAdv(testAdv);

        commentRequest = new CommentRequest("Great product!", 5);
        updateRequest = new CommentUpdateRequest("Updated comment!", 4);
    }

    // ==================== CREATE COMMENT TESTS ====================

    /**
     * Tests that a comment is successfully created on an advertisement when the user
     * is not the owner and has not already commented.
     */
    @Test
    void createComment_ShouldSucceed_WhenValid() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(false);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        var response = commentService.createComment(advId, commentRequest, userId);

        assertNotNull(response);
        assertEquals(testComment.getText(), response.text());
        assertEquals(testComment.getRate(), response.rate());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Tests that creating a comment fails when the user is the owner of the advertisement.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void createComment_ShouldThrowException_WhenUserOwnsAdvertisement() {
        testAdv.setUser(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ForbiddenException.class,
                () -> commentService.createComment(advId, commentRequest, userId));
    }

    /**
     * Tests that creating a comment fails when the user has already commented on the advertisement.
     * Expects a {@link ResourceAlreadyExistsException}.
     */
    @Test
    void createComment_ShouldThrowException_WhenUserAlreadyCommented() {
        when(advService.findAdvById(advId)).thenReturn(testAdv);
        when(commentRepository.existsByUserIdAndAdvId(userId, advId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> commentService.createComment(advId, commentRequest, userId));
    }

    // ==================== UPDATE COMMENT TESTS ====================

    /**
     * Tests that a comment is successfully updated when the user is the author.
     */
    @Test
    void updateComment_ShouldSucceed_WhenUserIsAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        var response = commentService.updateComment(1L, updateRequest, userId);

        assertNotNull(response);
        assertEquals("Updated comment!", response.text());
        assertEquals(4, response.rate());
        verify(commentRepository).save(testComment);
    }

    /**
     * Tests that updating a comment fails when the user is not the author.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void updateComment_ShouldThrowException_WhenUserNotAuthor() {
        UUID otherUserId = UUID.randomUUID();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        assertThrows(ForbiddenException.class,
                () -> commentService.updateComment(1L, updateRequest, otherUserId));
    }

    /**
     * Tests that updating a comment fails when the comment does not exist.
     * Expects a {@link ResourceNotFoundException}.
     */
    @Test
    void updateComment_ShouldThrowException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(1L, updateRequest, userId));
    }

    // ==================== DELETE COMMENT TESTS ====================

    /**
     * Tests that a comment is successfully deleted when the user is the author.
     */
    @Test
    void deleteComment_ShouldSucceed_WhenUserIsAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(1L, userId);

        verify(commentRepository).delete(testComment);
    }

    /**
     * Tests that deleting a comment fails when the user is not the author.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void deleteComment_ShouldThrowException_WhenUserNotAuthor() {
        UUID otherUserId = UUID.randomUUID();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        assertThrows(ForbiddenException.class,
                () -> commentService.deleteComment(1L, otherUserId));
    }

    /**
     * Tests that deleting a comment fails when the comment does not exist.
     * Expects a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteComment_ShouldThrowException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(1L, userId));
    }

    // ==================== GET COMMENTS TESTS ====================

    /**
     * Tests that retrieving comments for an advertisement returns a list.
     */
    @Test
    void getCommentsForAdv_ShouldReturnList() {
        when(commentRepository.findByAdvId(advId)).thenReturn(List.of(testComment));

        var results = commentService.getCommentsForAdv(advId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testComment.getText(), results.get(0).text());
    }

    /**
     * Tests that retrieving comments for an advertisement returns an empty list when there are none.
     */
    @Test
    void getCommentsForAdv_ShouldReturnEmptyList_WhenNoComments() {
        when(commentRepository.findByAdvId(advId)).thenReturn(List.of());

        var results = commentService.getCommentsForAdv(advId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}