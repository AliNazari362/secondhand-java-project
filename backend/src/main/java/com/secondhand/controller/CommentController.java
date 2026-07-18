package com.secondhand.controller;


import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.dto.comment.CommentUpdateRequest;
import com.secondhand.service.CommentService;
import com.secondhand.service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for comment operations on advertisements.
 *
 * <p>Provides endpoints for creating, updating, deleting, and retrieving comments
 * associated with a specific advertisement.
 * Base path: {@code /api/comments}</p>
 */
@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * Constructs a {@code CommentController} with the required service dependency.
     *
     * @param commentService the comment service used to handle comment business logic
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Creates a new comment on the specified advertisement by the authenticated user.
     *
     * @param advId   the UUID of the advertisement to comment on
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing the comment content
     * @return the {@link CommentResponse} representing the newly created comment
     */
    @PostMapping("{advId}/add-comment")
    public CommentResponse createComment(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @Valid @RequestBody CommentRequest request) {
        return commentService.createComment(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Updates an existing comment owned by the authenticated user.
     *
     * @param commentId the ID of the comment to update
     * @param token     the JWT bearer token from the {@code Authorization} request header
     * @param request   the validated request body containing the updated comment content
     * @return the updated {@link CommentResponse}
     */
    @PutMapping("{commentId}/update")
    public CommentResponse updateComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token, @Valid @RequestBody CommentUpdateRequest request) {
        return commentService.updateComment(commentId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Deletes a comment owned by the authenticated user.
     *
     * @param commentId the ID of the comment to delete
     * @param token     the JWT bearer token from the {@code Authorization} request header
     */
    @DeleteMapping("{commentId}/delete")
    public void deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token) {
        commentService.deleteComment(commentId, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves all comments associated with the specified advertisement.
     *
     * @param advId the UUID of the advertisement whose comments are to be retrieved
     * @return a list of {@link CommentResponse} objects for the specified advertisement
     */
    @GetMapping("{advId}/comments")
    public List<CommentResponse> getCommentForAdv(@PathVariable UUID advId) {
        return commentService.getCommentsForAdv(advId);
    }
}
