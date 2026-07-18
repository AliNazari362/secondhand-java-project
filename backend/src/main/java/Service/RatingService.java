package Service;

import DTO.comment.CommentRequest;
import DTO.comment.CommentResponse;
import Entity.Adv;
import Entity.Comment;
import Entity.User;
import Repository.CommentRepository;
import SpecialException.IllegalOwnershipException;
import SpecialException.RatingIsAlreadyExistException;

import java.util.UUID;

public class RatingService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;
    private final CommentService commentService;

    public RatingService(CommentRepository commentRepository,
                         AdvService advService,
                         UserService userService,
                         CommentService commentService) {
        this.commentRepository = commentRepository;
        this.advService = advService;
        this.userService = userService;
        this.commentService = commentService;
    }

    public CommentResponse rateAdvertisement(UUID advId, CommentRequest request, UUID userId) {
        Adv adv = advService.findAdvById(advId);

        if (adv.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("شما نمی توانید به آگهی خود امتیاز بدهید");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new RatingIsAlreadyExistException("برای این آگهی امتیاز ثبت کرده اید");
        }

        User rater = userService.findUserById(userId);

        Comment comment = new Comment(
                request.text(),
                request.rate(),
                rater,
                adv
        );

        Comment saved = commentRepository.save(comment);
        return commentService.toCommentResponse(saved);
    }

    public double getAverageRating(UUID advId) {
        Double avg = commentRepository.getAverageRatingByAdvId(advId);
        return avg != null ? avg : 0.0;
    }

    public long getRatingCount(UUID advId) {
        return commentRepository.countByAdvId(advId);
    }
}