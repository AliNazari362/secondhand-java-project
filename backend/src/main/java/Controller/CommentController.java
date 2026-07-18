package Controller;


import DTO.comment.CommentRequest;
import DTO.comment.CommentResponse;
import DTO.comment.CommentUpdateRequest;
import Service.CommentService;
import Service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("{advId}/add-comment")
    public CommentResponse createComment(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody CommentRequest request) {
        return commentService.createComment(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    @PutMapping("{commentId}/update")
    public CommentResponse updateComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token, @RequestBody CommentUpdateRequest request) {
        return commentService.updateComment(commentId, request, JwtUtil.getUserIdFromToken(token));
    }

    @DeleteMapping("{commentId}/delete")
    public void deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token) {
        commentService.deleteComment(commentId, JwtUtil.getUserIdFromToken(token));
    }

    @GetMapping("{advId}/comments")
    public List<CommentResponse> getCommentForAdv(@PathVariable UUID advId) {
        return commentService.getCommentsForAdv(advId);
    }
}
