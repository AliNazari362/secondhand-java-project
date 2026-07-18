package com.secondhand.service;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Comment;
import com.secondhand.entity.User;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for submitting and querying ratings on advertisements.
 * <p>
 * A rating is backed by a {@link Comment} entity that carries both a textual review
 * and a numeric score. Each user may rate a given advertisement at most once, and
 * advertisement owners cannot rate their own listings.
 * </p>
 * <p>
 * Aggregate statistics (average rating and total rating count) are also provided
 * to support display of summary scores on advertisement detail pages.
 * </p>
 */
@Service
public class RatingService {

    private final CommentRepository commentRepository;
    private final AdvService advService;
    private final UserService userService;
    private final CommentService commentService;

    /**
     * Constructs a {@code RatingService} with all required repository and service dependencies.
     *
     * @param commentRepository the repository for persisting and querying {@link Comment} entities
     * @param advService        the service used to look up advertisements by ID
     * @param userService       the service used to look up users by ID
     * @param commentService    the service used for converting comments to response DTOs
     */
    public RatingService(CommentRepository commentRepository,
                         AdvService advService,
                         UserService userService,
                         CommentService commentService) {
        this.commentRepository = commentRepository;
        this.advService = advService;
        this.userService = userService;
        this.commentService = commentService;
    }

    /**
     * Submits a rating (with an optional text review) for the specified advertisement.
     * <p>
     * Enforces the following rules:
     * <ul>
     *   <li>The rating user must not be the owner of the advertisement.</li>
     *   <li>Each user may only submit one rating per advertisement.</li>
     * </ul>
     * </p>
     *
     * @param advId   the UUID of the advertisement to rate
     * @param request the rating data including the numeric score and optional text review
     * @param userId  the UUID of the authenticated user submitting the rating
     * @return a {@link CommentResponse} representing the persisted rating/comment
     * @throws com.secondhand.exception.ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws ForbiddenException             if the user attempts to rate their own advertisement
     * @throws ResourceAlreadyExistsException if the user has already submitted a rating for this advertisement
     */
    public CommentResponse rateAdvertisement(UUID advId, CommentRequest request, UUID userId) {
        Adv adv = advService.findAdvById(advId);

        if (adv.getUser().getId().equals(userId)) {
            throw new ForbiddenException("شما نمی توانید به آگهی خود امتیاز بدهید");
        }

        if (commentRepository.existsByUserIdAndAdvId(userId, advId)) {
            throw new ResourceAlreadyExistsException("برای این آگهی امتیاز ثبت کرده اید");
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

    /**
     * Returns the average numeric rating for the specified advertisement.
     * <p>
     * Returns {@code 0.0} if no ratings have been submitted yet.
     * </p>
     *
     * @param advId the UUID of the advertisement whose average rating is requested
     * @return the average rating as a {@code double}, or {@code 0.0} if no ratings exist
     */
    public double getAverageRating(UUID advId) {
        Double avg = commentRepository.getAverageRatingByAdvId(advId);
        return avg != null ? avg : 0.0;
    }

    /**
     * Returns the total number of ratings submitted for the specified advertisement.
     *
     * @param advId the UUID of the advertisement whose rating count is requested
     * @return the number of ratings as a {@code long}; {@code 0} if none exist
     */
    public long getRatingCount(UUID advId) {
        return commentRepository.countByAdvId(advId);
    }
}
