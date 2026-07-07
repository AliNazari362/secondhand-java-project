package Service;   // پکیج را به حروف کوچک اصلاح کردم

import dto.comment.CommentRequest;
import dto.comment.CommentResponse;
import dto.comment.CommentUpdateRequest;
import dto.user.UserSummaryResponse;
import entity.Adv;               // ← این import را اضافه کن
import entity.Comment;
import entity.User;
import Repository.CommentRepository;   // ← پکیج را اصلاح کردم

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;

    // سازنده دستی (بدون لومبوک و بدون Spring)
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
            throw new RuntimeException("You cannot comment on your own advertisement");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new RuntimeException("You have already commented on this advertisement");
        }

        User author = userService.findUserById(userId);
        Comment comment = new Comment(request.text(), request.rate(), author, adv);
        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not the author of this comment");
        }

        comment.setText(request.text());
        comment.setRate(request.rate());
        Comment updated = commentRepository.save(comment);
        return toCommentResponse(updated);
    }

    public void deleteComment(Long commentId, UUID userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not the author of this comment");
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

    private CommentResponse toCommentResponse(Comment comment) {
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