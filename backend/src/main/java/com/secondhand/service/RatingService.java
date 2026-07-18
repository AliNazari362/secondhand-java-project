package com.secondhand.service;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Comment;
import com.secondhand.entity.User;
import com.secondhand.repository.CommentRepository;
import com.secondhand.exception.IllegalOwnershipException;
import com.secondhand.exception.RatingIsAlreadyExistException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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