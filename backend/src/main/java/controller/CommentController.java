package controller;

import dto.comment.CommentRequest;
import dto.comment.CommentResponse;
import dto.comment.CommentUpdateRequest;
import exception.IllegalTokenException;
import service.CommentService;
import service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/adv/{advId}")
    public CommentResponse createComment(@PathVariable UUID advId,
                                         @RequestHeader("Authorization") String header,
                                         @Valid @RequestBody CommentRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return commentService.createComment(advId, request, userId);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@PathVariable Long commentId,
                                         @RequestHeader("Authorization") String header,
                                         @Valid @RequestBody CommentUpdateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return commentService.updateComment(commentId, request, userId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        commentService.deleteComment(commentId, userId);
    }

    @GetMapping("/adv/{advId}")
    public List<CommentResponse> getCommentsForAdv(@PathVariable UUID advId) {
        return commentService.getCommentsForAdv(advId);
    }

    private UUID extractUserIdFromToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalTokenException("توکن نامعتبر است");
        }
        String token = header.substring(7);
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("توکن نامعتبر یا منقضی شده است");
        }
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        return UUID.fromString(userIdStr);
    }
}