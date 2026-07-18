package service;

import dto.comment.CommentRequest;
import dto.comment.CommentResponse;
import dto.user.UserSummaryResponse;
import entity.Adv;
import entity.Comment;
import entity.User;
import Repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RatingService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;

    public RatingService(CommentRepository commentRepository,
                         AdvService advService,
                         UserService userService) {
        this.commentRepository = commentRepository;
        this.advService = advService;
        this.userService = userService;
    }

    public CommentResponse rateAdvertisement(UUID advId, CommentRequest request, UUID userId) {
        Adv adv = advService.findAdvById(advId);

        if (adv.getUser().getId().equals(userId)) {
            throw new RuntimeException("You cannot rate your own advertisement");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new RuntimeException("You have already rated this advertisement");
        }

        User rater = userService.findUserById(userId);

        Comment comment = new Comment(
                request.text(),
                request.rate(),
                rater,
                adv
        );

        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    public double getAverageRating(UUID advId) {
        Double avg = commentRepository.getAverageRatingByAdvId(advId);
        return avg != null ? avg : 0.0;
    }

    public long getRatingCount(UUID advId) {
        return commentRepository.countByAdvId(advId);
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