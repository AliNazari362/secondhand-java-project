package Service;

import DTO.comment.CommentRequest;
import DTO.comment.CommentResponse;
import DTO.comment.CommentUpdateRequest;
import DTO.user.UserSummaryResponse;
import Entity.Adv;
import Entity.Comment;
import Entity.User;
import Repository.CommentRepository;
import SpecialException.CommentIsAlreadyExistException;
import SpecialException.IllegalOwnershipException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                          AdvService advService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.advService = advService;
        this.userService = userService;
    }

    public CommentResponse createComment(UUID advId, CommentRequest request, UUID userId) {
        Adv adv = advService.findAdvById(advId);

        if (adv.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("شما نمی توانید برای پست خود کامنت بگذارید");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new CommentIsAlreadyExistException("شما برای این پست نظر ثبت کرده اید");
        }

        User author = userService.findUserById(userId);
        Comment comment = new Comment(request.text(), request.rate(), author, adv);
        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("شما دسترسی برای ویرایش این نظر ندارید");
        }

        comment.setText(request.text());
        comment.setRate(request.rate());
        Comment updated = commentRepository.save(comment);
        return toCommentResponse(updated);
    }

    public void deleteComment(Long commentId, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("شما دسترسی برای حذف این نظر ندارید");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsForAdv(UUID advId) {
        return commentRepository.findByAdvId(advId).stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

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