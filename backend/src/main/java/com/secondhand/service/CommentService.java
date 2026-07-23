package com.secondhand.service;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.dto.comment.CommentUpdateRequest;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Comment;
import com.secondhand.entity.User;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing user comments on advertisements.
 * <p>
 * Supports creating, updating, and deleting comments, as well as retrieving
 * all comments for a given advertisement. Each user may only post one comment
 * per advertisement, and users cannot comment on their own advertisements.
 * </p>
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;

    /**
     * Constructs a {@code CommentService} with the required repository and service dependencies.
     *
     * @param commentRepository the repository for persisting and querying {@link Comment} entities
     * @param advService        the service used to look up advertisements by ID
     * @param userService       the service used to look up users by ID
     */
    public CommentService(CommentRepository commentRepository,
                          AdvService advService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.advService = advService;
        this.userService = userService;
    }

    /**
     * Creates a new comment on the specified advertisement.
     * <p>
     * Enforces the following rules:
     * <ul>
     *   <li>The commenting user must not be the owner of the advertisement.</li>
     *   <li>Each user may only submit one comment per advertisement.</li>
     * </ul>
     * </p>
     *
     * @param advId   the UUID of the advertisement to comment on
     * @param request the comment data including text and rating
     * @param userId  the UUID of the authenticated user submitting the comment
     * @return a {@link CommentResponse} representing the newly created comment
     * @throws ResourceNotFoundException      if no advertisement exists with the given {@code advId}
     * @throws ForbiddenException             if the user attempts to comment on their own advertisement
     * @throws ResourceAlreadyExistsException if the user has already commented on this advertisement
     */
    public CommentResponse createComment(UUID advId, CommentRequest request, UUID userId) {
        Adv adv = advService.findAdvById(advId);

        if (adv.getUser().getId().equals(userId)) {
            throw new ForbiddenException("شما نمی توانید برای پست خود کامنت بگذارید");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new ResourceAlreadyExistsException("شما برای این پست نظر ثبت کرده اید");
        }

        User author = userService.findUserById(userId);
        Comment comment = new Comment(request.text(), request.rate(), author, adv);
        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    /**
     * Updates the text and rating of an existing comment.
     * <p>
     * Only the original author of the comment may update it.
     * </p>
     *
     * @param commentId the numeric ID of the comment to update
     * @param request   the updated comment data including new text and rating
     * @param userId    the UUID of the authenticated user requesting the update
     * @return a {@link CommentResponse} reflecting the updated comment
     * @throws ResourceNotFoundException if no comment exists with the given {@code commentId}
     * @throws ForbiddenException        if the requesting user is not the original author
     */
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("شما دسترسی برای ویرایش این نظر ندارید");
        }

        comment.setText(request.text());
        comment.setRate(request.rate());
        Comment updated = commentRepository.save(comment);
        return toCommentResponse(updated);
    }

    /**
     * Deletes a comment permanently.
     * <p>
     * Only the original author of the comment may delete it.
     * </p>
     *
     * @param commentId the numeric ID of the comment to delete
     * @param userId    the UUID of the authenticated user requesting deletion
     * @throws ResourceNotFoundException if no comment exists with the given {@code commentId}
     * @throws ForbiddenException        if the requesting user is not the original author
     */
    public void deleteComment(Long commentId, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("شما دسترسی برای حذف این نظر ندارید");
        }

        commentRepository.delete(comment);
    }

    /**
     * Returns all comments associated with the given advertisement.
     *
     * @param advId the UUID of the advertisement whose comments should be retrieved
     * @return a list of {@link CommentResponse} objects for the advertisement;
     *         never {@code null}, may be empty
     */
    public List<CommentResponse> getCommentsForAdv(UUID advId) {
        return commentRepository.findByAdvId(advId).stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Looks up a comment by its numeric ID, throwing if not found.
     *
     * @param commentId the numeric ID of the comment to find
     * @return the {@link Comment} entity with the given ID
     * @throws ResourceNotFoundException if no comment exists with the given {@code commentId}
     */
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("نظر مورد نظر یافت نشد"));
    }

    /**
     * Converts a {@link Comment} entity to a {@link CommentResponse} DTO.
     * <p>
     * Includes the author's summary information alongside the comment content and rating.
     * </p>
     *
     * @param comment the comment entity to convert
     * @return a {@link CommentResponse} populated with the comment's data and author summary
     */
    public CommentResponse toCommentResponse(Comment comment) {
        User author = comment.getUser();
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getRate(),
                new UserSummaryResponse(
                        author.getId(),
                        author.getFullName(),
                        author.getEmail(),
                        author.getUserType()
                ),
                comment.getDate()
        );
    }
}