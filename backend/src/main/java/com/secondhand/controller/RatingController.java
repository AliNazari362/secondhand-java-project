package com.secondhand.controller;

import com.secondhand.dto.comment.CommentRequest;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.service.JwtUtil;
import com.secondhand.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for rating advertisements.
 *
 * <p>Provides endpoints for submitting a rating on an advertisement and for querying
 * the aggregate rating statistics (average and count) for a given advertisement.
 * Base path: {@code /api/ratings}</p>
 */
@RestController
@RequestMapping("api/ratings")
public class RatingController {

    private final RatingService ratingService;

    /**
     * Constructs a {@code RatingController} with the required service dependency.
     *
     * @param ratingService the rating service used to handle rating business logic
     */
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Submits a rating for the specified advertisement by the authenticated user.
     *
     * @param advId   the UUID of the advertisement to rate
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing the rating score and optional comment
     * @return the {@link CommentResponse} representing the persisted rating entry
     */
    @PostMapping("{advId}")
    public CommentResponse rateAdvertisement(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @Valid @RequestBody CommentRequest request) {
        return ratingService.rateAdvertisement(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves the average rating score for the specified advertisement.
     *
     * @param advId the UUID of the advertisement
     * @return the average rating as a {@code double}; returns {@code 0.0} if no ratings exist
     */
    @GetMapping("/{advId}/average")
    public double getAverageRating(@PathVariable UUID advId) {
        return ratingService.getAverageRating(advId);
    }

    /**
     * Retrieves the total number of ratings submitted for the specified advertisement.
     *
     * @param advId the UUID of the advertisement
     * @return the total rating count as a {@code long}
     */
    @GetMapping("/{advId}/count")
    public long getRatingCount(@PathVariable UUID advId) {
        return ratingService.getRatingCount(advId);
    }

}